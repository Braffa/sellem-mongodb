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
                    updDate: DateTime) {

  override def toString: String = {
    var p = "Product \n[BSONObjectID  - " + BSONObjectID
    p += "\n author        - " + author
    p += "\n imageURL      - " + imageURL
    p += "\n imageLargeURL - " + imageLargeURL
    p += "\n manufacturer  - " + manufacturer
    p += "\n productIndex  - " + productIndex
    p += "\n productgroup  - " + productgroup
    p += "\n productId     - " + productId
    p += "\n productidtype - " + productidtype
    p += "\n source        - " + source
    p += "\n sourceid      - " + sourceid
    p += "\n title         - " + title
    p += "\n crDate        - " + crDate
    p += "\n updDate       - " + updDate + "\n]"
    p
  }

  def delimit: String = {
    var p = author
    p += ", " + imageURL
    p += ", " + imageLargeURL
    p += ", " + manufacturer
    p += ", " + productIndex
    p += ", " + productgroup
    p += ", " + productId
    p += ", " + productidtype
    p += ", " + source
    p += ", " + sourceid
    p += ", " + title
    p
  }

}

object ProductJsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._
  import play.modules.reactivemongo.json.BSONFormats._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val productFormat = Json.format[Product]
}