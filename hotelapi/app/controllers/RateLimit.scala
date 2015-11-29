package controllers

import org.joda.time.DateTime
import play.api.{Play, Logger}
import play.api.mvc.{Result, ActionBuilder, WrappedRequest, Request}
import play.api.mvc.Results._
import play.api.Play.current
import scala.collection.concurrent.TrieMap
import scala.concurrent.Future

/**
 * Created by fatihdonmez on 29/11/15
 */

/**
 * Helper case class to reuse api keys.
 * It has optional rate limit which supports different time limits for individual keys
 * If its not set, application.conf has a default value
 * @param key
 * @param lastRequest
 * @param blocked
 * @param rateLimit
 */
case class RateLimitedKey(key: String, lastRequest: Long, blocked: Boolean, rateLimit: Option[Int])

class RateLimitRequest[A](val key: RateLimitedKey, request: Request[A]) extends WrappedRequest[A](request)

/**
 * Secure Action.
 * Composing any controller action with Secure make api will be key based as defined in specs.
 */
object Secure extends ActionBuilder[RateLimitRequest] {

  //Concurrent thread safe map for keys
  val keyAccessMap = TrieMap[String, RateLimitedKey]()

  // If configuration is not set
  val DefaultBlockingDuration = 60000

  // If configuration is not set
  val DefaultRateLimit = 10000

  val AuthHeaderKey = "X-APIKEY"

  def invokeBlock[A](request: Request[A], block: (RateLimitRequest[A]) => Future[Result]) = {

    request.headers.get(AuthHeaderKey) match {
      case Some(keyStr) => {
        keyAccessMap.get(keyStr) match {
          case Some(key) => {

            val currentTime = DateTime.now().getMillis
            val blockingDuration = Play.configuration.getInt("hotelapi.blockingDuration").getOrElse(DefaultBlockingDuration)

            if (key.blocked) {
              //Unblock api key
              if ((key.lastRequest + blockingDuration) < currentTime) {

                //Update last request time and unblock api key
                keyAccessMap.put(key.key, key.copy(blocked = false, lastRequest = currentTime))
                block(new RateLimitRequest(key, request))

              } else {
                errorResult("Api key is blocked, Time left for unblock [" + (key.lastRequest + blockingDuration - currentTime) + "ms]")
              }
            } else {

              val rateLimit = key.rateLimit.getOrElse(Play.configuration.getInt("hotelapi.rateLimit").getOrElse(DefaultRateLimit))

              if ((key.lastRequest + rateLimit) > currentTime) {
                //Block key
                keyAccessMap.put(key.key, key.copy(blocked = true, lastRequest = currentTime))
                errorResult("Api key will be blocked for next [" + blockingDuration + "ms]")
              } else {

                //Update last request time
                keyAccessMap.put(key.key, key.copy(lastRequest = currentTime))
                block(new RateLimitRequest(key, request))
              }
            }
          }
          case None => errorResult("Unauthorized")
        }
      }
      case None => errorResult("Unauthorized")
    }
  }

  private def errorResult(error: String): Future[Result] = Future.successful(Unauthorized( s"""{"message" : "$error"}""").as("application/json"))
}


