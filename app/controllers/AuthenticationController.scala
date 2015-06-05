package controllers

import org.joda.time.DateTime

import models.authentication.RegisteredUser
import models.authentication.RegisteredUserJsonFormats.registeredUserFormat
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Future
import scala.io.Source

object AuthenticationController extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("registeredUsers")

  def login (userId: String) = Action.async {
    Logger.info("AuthenticationController login userId = " + userId)

    val logins: Future[List[RegisteredUser]]
      = collection.find(Json.obj("userId" -> userId)).cursor[RegisteredUser].collect[List]()

    logins.map { result =>
      val json = Json.toJson(result)
      Logger.info("json " + json)
      Ok(json)
    }
  }

  def findRegisteredUser(attribute: String, filter: String) = Action.async {
    Logger.info("findRegisteredUser")
    val registeredUser: Future[List[RegisteredUser]] = collection.find(Json.obj(attribute -> filter)).cursor[RegisteredUser].collect[List]()

    registeredUser.map { result =>
     //Logger.info("*******************")
     // Logger.info(result)
     // Logger.info("*******************")
    }
    registeredUser.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def registerUser (authorityLevel: String, email: String, firstName: String, lastName: String, password: String, telephone: String, userId: String)
    = {
    Logger.info("AuthenticationController  registerUser")
    val registeredUser = new RegisteredUser (BSONObjectID.generate,
      authorityLevel,
      email,
      firstName,
      lastName,
      password,
      telephone,
      userId,
      new DateTime(),
      new DateTime())
    Logger.info (registeredUser.toString)
    val futureUpdateLogin = collection.insert(registeredUser)
    futureUpdateLogin.map { result =>
      Logger.info("success " + registeredUser.userId + " has been created")
      Logger.info("**** " + result + "*****")
    }.recover {
      case e => Logger.info(e.getMessage())
    }
    listRegisteredUsers

  }

  def listRegisteredUsers = Action.async {
    Logger.info("listRegisteredUsers")
    val registeredUsers: Future[List[RegisteredUser]] = collection.genericQueryBuilder.cursor[RegisteredUser].collect[List]()
    registeredUsers.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def listJsonRegisteredUsers = Action.async {
    Logger.info("listJsonRegisteredUsers")
    val registeredUsers: Future[List[RegisteredUser]] = collection.genericQueryBuilder.cursor[RegisteredUser].collect[List]()
    registeredUsers.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def reloadTestData = {
    Logger.info("reloadTestData")
    collection.drop()
    val src = Source.fromFile(".\\resources\\registeredUsers.txt").getLines
    val headerLine = src.take(1).next
    for(l <- src) {
      var registeredUserElement = new Array[String](11)
      var index = 0
      l.split(", ").map { c =>
        registeredUserElement(index) = c.trim
        index += 1
      }
      val registeredUser = new RegisteredUser(BSONObjectID.generate,
        registeredUserElement (0),
        registeredUserElement (1),
        registeredUserElement (2),
        registeredUserElement (3),
        registeredUserElement (4),
        registeredUserElement (5),
        registeredUserElement (6),
        new DateTime(),
        new DateTime())
      Logger.info(registeredUser.toString)
      val futureRegisteredUser = collection.insert(registeredUser)
    }
    listRegisteredUsers
  }

}