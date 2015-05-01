package controllers.authentication

import scala.concurrent.Future
import java.util.concurrent.TimeoutException

import org.joda.time.DateTime

import play.api.libs.json._
import play.api.mvc._
import play.api.Logger

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.bson.BSONObjectID

import models.authentication.Login
import models.authentication.JsonFormats.loginFormat

object LoginController extends Controller with MongoController {
  def collection: JSONCollection = db.collection[JSONCollection]("login")

  def saveLogin = Action {
    val login = new Login(BSONObjectID.generate, "99", "password7", "userid7", new DateTime(), new DateTime())
    val futureUpdateLogin = collection.insert(login)
    futureUpdateLogin.map { result =>
      Logger.info("success " + login.userId + " has been created")
      Logger.info("**** " + result + "*****")
    }.recover {
      case e => Logger.error(e.getMessage())
    }

    Ok("Got Here " + login.userId)
  }

  def listLogins = Action.async {
    val logins: Future[List[Login]] = collection.genericQueryBuilder.cursor[Login].collect[List]()
    logins.map { result =>
      Ok(Json.toJson(result))
    }
  }
}