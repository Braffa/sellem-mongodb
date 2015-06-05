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
                   updDate: DateTime) {
  override def toString: String = {
    var p = "\nRegisteredUser "
    p += "\n[BSONObjectID   - " + BSONObjectID
    p += "\n authorityLevel - " + authorityLevel
    p += "\n email          - " + email
    p += "\n firstName      - " + firstName
    p += "\n lastName       - " + lastName
    p += "\n password       - " + password
    p += "\n telephone      - " + telephone
    p += "\n userId         - " + userId
    p += "\n crDate         - " + crDate
    p += "\n updDate        - " + updDate + "\n]"
    p
  }
}
                   
object RegisteredUserJsonFormats {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._
  import play.modules.reactivemongo.json.BSONFormats._

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val registeredUserFormat = Json.format[RegisteredUser]
}
