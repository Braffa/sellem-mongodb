package models.authentication

import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime

import play.api.libs.json.Json

case class Login (_id: BSONObjectID,
                   authorityLevel: String, 
                   password: String,
                   userId: String,
                   crDate: DateTime,
                   updDate: DateTime)
object JsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._
  import play.modules.reactivemongo.json.BSONFormats._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val loginFormat = Json.format[Login]
}
