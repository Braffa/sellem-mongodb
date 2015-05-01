package controllers.authentication

import scala.concurrent.Future
import java.util.concurrent.TimeoutException

import org.joda.time.DateTime

// Java
//import play.mvc.Result;// Scala
import play.api.mvc.Result


import play.api.libs.json._
import play.api.mvc._
import play.api.Logger

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.bson.BSONObjectID

import models.authentication.RegisteredUser
import models.authentication.RegisteredUserJsonFormats.registeredUserFormat

object RegisteredUserController extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("registeredUsers")

  def setUpRegisteredUser: RegisteredUser = {
    println("setUpRegisteredUser")
    val registeredUser = new RegisteredUser (BSONObjectID.generate,
      "99",
      "daveAllan@yahoo.com",
      "dave",
      "alan",
      "password8",
      "01388 898989",
      "userid8",
      new DateTime(),
      new DateTime())
    registeredUser
  }

  //http://localhost:9000/findregistereduser?attribute=userId&filter=userid8
  def findRegisteredUser(attribute: String, filter: String) = Action.async {
    println("findRegisteredUser")
    val registeredUser: Future[List[RegisteredUser]] = collection.find(Json.obj(attribute -> filter)).cursor[RegisteredUser].collect[List]()

    registeredUser.map { result =>
      println("*******************")
      println(result)
      println("*******************")
    }
    registeredUser.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def saveRegisteredUser = Action {
    val registeredUser = setUpRegisteredUser

    val futureUpdateLogin = collection.insert(registeredUser)
    futureUpdateLogin.map { result =>
      Logger.info("success " + registeredUser.userId + " has been created")
      Logger.info("**** " + result + "*****")
    }.recover {
      case e => Logger.error(e.getMessage())
    }

    Ok("Got Here " + registeredUser.userId)
  }

  def listRegisteredUsers = Action.async {
    println("listRegisteredUsers")
    val registeredUsers: Future[List[RegisteredUser]] = collection.genericQueryBuilder.cursor[RegisteredUser].collect[List]()
    registeredUsers.map { result =>
      Ok(Json.toJson(result))
    }
  }
}