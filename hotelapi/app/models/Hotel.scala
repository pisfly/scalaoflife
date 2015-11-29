package models

import play.api.libs.json._

/**
 * Created by fatihdonmez on 29/11/15
 */
case class Hotel(hotelId: Int, room: String, price: Double, city: String)

object HotelJsonParser {

  implicit val hotelWrites = new Writes[Hotel] {
    def writes(hotel: Hotel) = Json.obj(
      "city" -> hotel.city,
      "hotelId" -> hotel.hotelId,
      "room" -> hotel.room,
      "price" -> hotel.price
    )
  }

  implicit val hotelReads = new Reads[Hotel] {

    def reads(json: JsValue) = JsSuccess(
      Hotel(
        (json \ "hotelId").as[Int],
        (json \ "room").as[String],
        (json \ "price").as[Double],
        (json \ "city").as[String]
      )
    )
  }
}
