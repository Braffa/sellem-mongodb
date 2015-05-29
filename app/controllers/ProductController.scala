package controllers

import com.sun.corba.se.spi.ior.ObjectId
import models.product.{UserToProduct, Product}
import models.product.ProductJsonFormats.productFormat
import models.product.UserToProductJsonFormats.userToProductFormat

import scala.collection.immutable.List
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{ListBuffer}

import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json._
import reactivemongo.bson.{BSON, BSONRegex, BSONDocument, BSONObjectID}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.io.Source


/**
 * Created by david.a.brayfield on 09/04/2015.
 */
object ProductController  extends Controller with MongoController {

  def productCollection: JSONCollection = db.collection[JSONCollection]("product")
  def userToProductCollection: JSONCollection = db.collection[JSONCollection]("userToProduct")

  def convertDate (strDate: String): DateTime = new DateTime(java.lang.Long.parseLong(strDate))

  def setUpProduct: Product = {
    Logger.info("setUpProduct")

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
    Logger.info("findProduct attribute = " + attribute + " filter = " + filter)
    val product: Future[List[Product]] = productCollection.find(Json.obj(attribute -> filter)).cursor[Product].collect[List]()
    product.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def getProductIds(lOfUserToProducts: List[UserToProduct]): ListBuffer[String] = {
    Logger.info("getProductIds")
    var lOfProductIds = ListBuffer [String]()
    for(userToProduct <- lOfUserToProducts) {
      Logger.info(userToProduct.userId + " - " + userToProduct.productId)
      lOfProductIds += userToProduct.productId
    }
    lOfProductIds
  }

  def getJasonString (lOfProducts: ListBuffer[String]): String = {
    Logger.info("getJasonString")
    var jsonString = "{ \"$query\":  { \"productId\": { \"$in\": ["
    var index = 1
    lOfProducts.foreach { i =>
      jsonString += "\"" + i + "\""
      if (index < lOfProducts.size) {
        jsonString += ", "
        index += 1
      }
    }
    jsonString += "] }}, \"$orderby\": { \"author\": 1} } "
    Logger.info(jsonString)
    jsonString
  }

  def findMyProducts(userId: String) = Action.async {
    Logger.info("findMyProducts userId " + userId)
    var lOfProducts = ListBuffer [String]()
    for {
      lOfUserToProducts <- userToProductCollection.find(Json.obj("userId" -> userId)).cursor[UserToProduct].collect[List]()
      lOfProducts = getProductIds(lOfUserToProducts)
      jsonString = getJasonString (lOfProducts)
      json = play.api.libs.json.Json.parse(jsonString)
      lOfProducts <- productCollection.find(json).cursor[Product].collect[List]()
    } yield {
      Logger.info(""+lOfProducts.size)
      for (product <- lOfProducts) println(product.toString)
      Ok(Json.toJson(lOfProducts))
    }
  }

  def listProducts = Action.async {
    val products: Future[List[Product]] = productCollection.genericQueryBuilder.cursor[Product].collect[List]()
    products.map { result =>
      Ok(views.html.listproducts("List of All products")((result)))
    }
  }

  def listJsonProducts = Action.async {
    Logger.info("listJsonProducts ")
    val products: Future[List[Product]] = productCollection.genericQueryBuilder.cursor[Product].collect[List]()
    products.map { result =>
      Ok(Json.toJson(result))
    }
  }
  

  def searchCatalogue (author: String, title: String, productId: String, manufacturer: String)
    =  Action.async {
    Logger.info("ProductController  searchCatalogue")
    var index = 0
    var parameterMap : mutable.Map[String, String] = mutable.Map()
    if (author.length > 0 ) {
      parameterMap += ("author" -> author)
      index += 1
    }
    if (title.length > 0 ) {
      parameterMap += ("title" -> title)
      index += 1
    }
    if (productId.length > 0) {
      parameterMap += ("productId" -> productId)
      index += 1
    }
    if (manufacturer.length > 0) {
      parameterMap += ("manufacturer" -> manufacturer)
    }
    var jsonString = "{ "
    index = 0
    parameterMap.keys.foreach{ i =>
      Logger.info( "Key = " + i + " Value = " + parameterMap(i))
      jsonString += "\"" + i + "\" : {\"$regex\" : \".*" + parameterMap(i) + ".*\","
      jsonString += "\"" + "$options" + "\" : \"i\" }"
      index += 1
      if (parameterMap.size > 1 && index < parameterMap.size) {
        jsonString += " , "
      }
    }
    jsonString += "}"
    Logger.info(jsonString)
    val json = play.api.libs.json.Json.parse(jsonString)
    val products: Future[List[Product]] =
      productCollection.find(json).cursor[Product].collect[List]()
    products.map { result =>
      println(Json.toJson(result))
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

  def reloadTestData = {
    Logger.info("reloadTestData")
    productCollection.drop()
    val src = Source.fromFile(".\\resources\\products.txt").getLines
    val headerLine = src.take(1).next
    for(l <- src) {
      var productElement = new Array[String](11)
      var index = 0
      l.split(", ").map { c =>
        productElement(index) = c.trim
        index += 1
      }
      val product = new Product (BSONObjectID.generate,
        productElement (0),
        productElement (1),
        productElement (2),
        productElement (3),
        productElement (4),
        productElement (5),
        productElement (6),
        productElement (7),
        productElement (8),
        productElement (9),
        productElement (10),
        new DateTime(),
        new DateTime())
      Logger.info(product.toString)
      val futureProduct = productCollection.insert(product)
    }
    listProducts
  }

}
