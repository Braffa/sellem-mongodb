package models.product

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID

case class Product (_id: BSONObjectID,
                    author: String,
                    imageURL: String,
                    imageLargeURL: String,
                    manufacturer: String,
                    productIndex: String,
                    productgroup: String,
                    productId: String,
                    productidtype: String,
                    source: String,
                    sourceid: String,
                    title: String,
                    crDate: DateTime,
                    updDate: DateTime)

object ProductJsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._
  import play.modules.reactivemongo.json.BSONFormats._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val productFormat = Json.format[Product]
}