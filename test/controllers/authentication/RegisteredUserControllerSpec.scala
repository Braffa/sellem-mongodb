package controllers.authentication

import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._


import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.specs2.time.DurationConversions

import play.api.Logger
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.ws._
import play.api.mvc._

import models.authentication.RegisteredUserJsonFormats.registeredUserFormat

class RegisteredUserControllerSpec  extends FlatSpec with
    MockitoSugar with Matchers with BeforeAndAfter with DurationConversions {
  //implicit val Timeout = 10 seconds
  //override implicit def defaultAwaitTimeout: Timeout = 20.seconds

  println("Starting tests")
  println()
  "A registered user " should "be returned " in {
    val registeredUser = RegisteredUserController.setUpRegisteredUser
    registeredUser.authorityLevel should be ("99")
    //registeredUser.crDate  should be ("dave")
    registeredUser.email  should be ("daveAllan@yahoo.com")
    registeredUser.firstName should be ("dave")
    registeredUser.lastName  should be ("alan")
    registeredUser.password  should be ("password8")
    registeredUser.telephone  should be ("01388 898989")
    //registeredUser.updDate  should be ("dave")
    registeredUser.userId  should be ("userid8")
  }

  it should "Try and call the method" in new WithApplication(new FakeApplication()) {

    val result = Await.result(RegisteredUserController.listRegisteredUsers(FakeRequest()), 10 seconds)
    result.toString
    println("** " + result)

  }
}