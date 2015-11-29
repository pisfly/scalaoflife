import controllers.Secure
import models.{Hotel, HotelRepository}
import models.HotelJsonParser._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.Play

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beSome.which(status(_) == NOT_FOUND)
    }

    "have hotel repository" in new WithApplication {
      HotelRepository.getSize must be_>(0)
    }

    "has working auth api" in new WithApplication {
      route(FakeRequest(GET, "/auth")) must beSome.which(status(_) == OK)
    }

    "forbids to access search api without api key" in new WithApplication {
      route(FakeRequest(GET, "/search/Bangkok")) must beSome.which(status(_) == UNAUTHORIZED)
    }

    "allow to access search api with api key" in new WithApplication {

      val authKey = getAuthKey
      val hotelList = apiQuery(authKey, "/search/Bangkok")
      hotelList.size must be_>(0)
    }

    "has search api with ASC sorting support" in new WithApplication {

      val authKey = getAuthKey
      val hotelList = apiQuery(authKey, "/search/Bangkok?sort=ASC")
      val isAsc = hotelList.view.zip(hotelList.tail).forall(x => x._1.price <= x._2.price)

      isAsc must beTrue
    }

    "has search api with DESC sorting support" in new WithApplication {
      val authKey = getAuthKey
      val hotelList = apiQuery(authKey, "/search/Bangkok?sort=DESC")
      val isAsc = hotelList.view.zip(hotelList.tail).forall(x => x._1.price >= x._2.price)

      isAsc must beTrue
    }

    "block requests which exceeding rate limit" in new WithApplication {

      val authKey = getAuthKey
      val hotelList = apiQuery(authKey, "/search/Bangkok")
      val secondApiCall = route(FakeRequest(GET, "/search/Bangkok").withHeaders(Secure.AuthHeaderKey -> authKey)).get
      val secondApiCallResult = (contentAsJson(secondApiCall) \ "message").as[String]

      secondApiCallResult must contain("Api key will be blocked for next")
    }

    "allow requests which obeying rate limit" in new WithApplication {

      val authKey = getAuthKey
      val hotelList = apiQuery(authKey, "/search/Bangkok")

      //Wait a little bit
      Thread sleep Play.configuration.getLong("hotelapi.rateLimit").getOrElse(Secure.DefaultRateLimit)

      val secondApiCall = route(FakeRequest(GET, "/search/Bangkok").withHeaders(Secure.AuthHeaderKey -> authKey)).get
      val secondApiCallResult = contentAsJson(secondApiCall).as[List[Hotel]]

      secondApiCallResult.size must equalTo(hotelList.size)
    }

    "allow blocking requests after timeout" in new WithApplication {

      val authKey = getAuthKey
      val hotelList = apiQuery(authKey, "/search/Bangkok")
      val secondApiCall = route(FakeRequest(GET, "/search/Bangkok").withHeaders(Secure.AuthHeaderKey -> authKey)).get
      val secondApiCallResult = (contentAsJson(secondApiCall) \ "message").as[String]

      secondApiCallResult must contain("Api key will be blocked for next")

      //Wait a little bit
      Thread sleep Play.configuration.getLong("hotelapi.blockingDuration").getOrElse(Secure.DefaultBlockingDuration)

      //This should be valid request
      val thirdApiCall = route(FakeRequest(GET, "/search/Bangkok").withHeaders(Secure.AuthHeaderKey -> authKey)).get
      val thirdApiCallResult = contentAsJson(thirdApiCall).as[List[Hotel]]

      thirdApiCallResult.size must equalTo(hotelList.size)
    }

  }

  private def getAuthKey: String = {
    val authResult = route(FakeRequest(GET, "/auth")).get
    (contentAsJson(authResult) \ s"""${Secure.AuthHeaderKey}""").as[String]
  }

  private def apiQuery(authKey: String, url: String): List[Hotel] = {
    val apiResult = route(FakeRequest(GET, url).withHeaders(Secure.AuthHeaderKey -> authKey)).get
    (contentAsJson(apiResult)).as[List[Hotel]]
  }
}
