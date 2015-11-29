import models.HotelRepository
import play.api._

import scala.io.{Codec, Source}

/**
 * Created by fatihdonmez on 29/11/15
 */
object Global extends GlobalSettings {

  override def onStart(application: Application): Unit = {
    //Init hotel repository with data
    HotelRepository("./conf/data/hoteldb.csv")
  }
}
