package controllers

import models.product.UserToProduct
import models.product.UserToProductJsonFormats.userToProductFormat
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

import scala.concurrent.Future
import scala.io.Source

/**
 * Created by david.a.brayfield on 09/04/2015.
 */
object UserToProductController  extends Controller with MongoController {

  def userToProductCollection: JSONCollection = db.collection[JSONCollection]("userToProduct")

  def listUserToProducts = Action.async {
    val userToProducts: Future[List[UserToProduct]] = userToProductCollection.genericQueryBuilder.cursor[UserToProduct].collect[List]()
    userToProducts.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def reloadUserToProductTestData = {
    Logger.info("reloadUserToProductTestData")
    val src = Source.fromFile(".\\resources\\usertoproducts.txt").getLines
    val headerLine = src.take(1).next
    for(l <- src) {
      var userToProductElement = new Array[String](3)
      var index = 0
      l.split(", ").map { c =>
        userToProductElement(index) = c.trim
        index += 1
      }
      val userToProduct = new UserToProduct (BSONObjectID.generate,
        userToProductElement (0),
        userToProductElement (1),
        userToProductElement (2),
        new DateTime(),
        new DateTime())
      Logger.info(userToProduct.toString)
      val futureProduct = userToProductCollection.insert(userToProduct)
    }
    listUserToProducts
  }
}
