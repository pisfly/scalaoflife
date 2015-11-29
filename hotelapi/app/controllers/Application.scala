package controllers

import java.security.SecureRandom

import models.{Hotel, HotelRepository}
import models.HotelJsonParser._
import org.apache.commons.codec.binary.Base64
import play.api.libs.json._
import play.api.mvc._

class Application extends Controller {

  val r = new SecureRandom

  /**
   * Search api for hotel repository
   * It supports sorting as option
   * @param city
   * @param sort
   * @return
   */
  def index(city: String, sort: Option[String]) = Secure { implicit request =>

    var hotels = HotelRepository.getByCityId(city)

    hotels = sort match {
      case Some(s) => {
        s match {
          case "ASC" => hotels.sortWith(_.price < _.price)
          case "DESC" => hotels.sortWith(_.price > _.price)
        }
      }
      case None => hotels
    }

    Ok(Json.toJson(hotels))

  }

  /**
   * Create auth key for api queries
   * @return
   */
  def auth = Action {

    val salt = new Array[Byte](24);
    r.nextBytes(salt);
    val keyStr = Base64.encodeBase64String(salt)

    Secure.keyAccessMap.putIfAbsent(keyStr, RateLimitedKey(keyStr,0,false,None))

    Ok(s"""{"${Secure.AuthHeaderKey}":"$keyStr"}""").as("application/json")
  }

}
