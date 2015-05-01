package models.product

import org.joda.time.DateTime
import reactivemongo.bson.BSONObjectID

/**
 * Created by david.a.brayfield on 09/04/2015.
 */
case class UserToProduct (_id: BSONObjectID,
                     productId: String,
                     productIndex: String,
                     userId: String,
                     crDate: DateTime,
                     updDate: DateTime)

object UserToProductJsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._
  import play.modules.reactivemongo.json.BSONFormats._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val userToProductFormat = Json.format[UserToProduct]


}


