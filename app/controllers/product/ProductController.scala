package controllers.product

import scala.concurrent.Future

import org.joda.time.DateTime
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

import models.product.UserToProduct
import models.product.UserToProductJsonFormats.userToProductFormat
import models.product.Product
import models.product.ProductJsonFormats.productFormat


/**
 * Created by david.a.brayfield on 09/04/2015.
 */
object ProductController  extends Controller with MongoController {

  def productCollection: JSONCollection = db.collection[JSONCollection]("product")
  def userToProductCollection: JSONCollection = db.collection[JSONCollection]("userToProduct")

  def setUpUserToProduct: UserToProduct = {
    println("setUserToProduct")
    val userToProduct = new UserToProduct (BSONObjectID.generate,
      "",
      "0",
      "userid8",
      new DateTime(),
      new DateTime())
    userToProduct
  }

  def setUpProduct: Product = {
    println("setUpProduct")
    val product = new Product (BSONObjectID.generate,
      "Fleming", //author: String,
      "http://ecx.images-amazon.com/images/I/71Z8j-Y5qAL._SL500_PIsitb-sticker-arrow-big,TopRight,35,-73_SL133_OU02_.jpg", //imageURL: String,
      "http://ecx.images-amazon.com/images/I/71Z8j-Y5qAL._SL500_PIsitb-sticker-arrow-big,TopRight,35,-73_SL133_OU02_.jpg", //imageLargeURL: String,
      "chocolate Productions", //manufacturer: String,
      "0", //productIndex: String,
      "book", //productgroup: String,
      "978-1780671062", //productId: String,
      "ISBN-13", //productidtype: String,
      "amazon", //source: String,
      "23", //sourceid: String,
      "An Inky Treasure Hunt and Colouring ", //title: String,
      new DateTime(),
      new DateTime())
    product
  }

  def findProduct(attribute: String, filter: String) = Action.async {
    println("findProduct attribute = " + attribute + " filter = " + filter)
    val product: Future[List[Product]] = productCollection.find(Json.obj(attribute -> filter)).cursor[Product].collect[List]()

    product.map { result =>
      println("*******************")
      println(result)
      println("*******************")
    }
    product.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def listProducts = Action.async {
    val products: Future[List[Product]] = productCollection.genericQueryBuilder.cursor[Product].collect[List]()
    products.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def saveProduct = Action {
    Logger.info("saveProduct")
    val product = setUpProduct
    Logger.info("saveProduct " + product.author)
    Logger.info("saveProduct " + product._id)
    val futureProduct = productCollection.insert(product)

    futureProduct.map { result =>
      Logger.info("success " + product.author + " has been created")
      Logger.info("**** " + result + "*****")
    }.recover {
      case e => Logger.error(e.getMessage())
    }
    Ok("Got Here " + product.author)
  }
}
