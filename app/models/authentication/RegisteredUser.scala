package models.authentication

import reactivemongo.bson.BSONObjectID
import org.joda.time.DateTime

import play.api.libs.json.Json

case class RegisteredUser (_id: BSONObjectID,
                   authorityLevel: String, 
                   email: String, 
                   firstName: String, 
                   lastName: String, 
                   password: String,
                   telephone: String, 
                   userId: String,
                   crDate: DateTime,
                   updDate: DateTime)
                   
object RegisteredUserJsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._
  import play.modules.reactivemongo.json.BSONFormats._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val registeredUserFormat = Json.format[RegisteredUser]
}
