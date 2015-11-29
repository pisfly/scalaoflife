package models

import scala.collection.mutable.ListBuffer
import scala.io.{Codec, Source}

/**
 * Created by fatihdonmez on 29/11/15
 */
object HotelRepository {

  private val data: ListBuffer[Hotel] = ListBuffer()

  def apply(path: String) {

    data.clear

    for (line <- Source.fromFile(path)(Codec.UTF8).getLines) {
      val tokens = line.split(",")

      val hotel = Hotel(tokens(1).toInt, tokens(2), tokens(3).toDouble, tokens(0))
      data += hotel
    }
  }

  def getByCityId(cityId: String): List[Hotel] = {
    data.filter(h => h.city.equalsIgnoreCase(cityId)).toList
  }

  def getSize: Int = data.length
}
