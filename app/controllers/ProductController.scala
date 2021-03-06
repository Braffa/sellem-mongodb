package controllers

import java.io.{FileWriter, BufferedWriter}

import models.product.{UserToProduct, Product}
import models.product.ProductJsonFormats.productFormat
import models.product.UserToProductJsonFormats.userToProductFormat
import reactivemongo.bson.Producer.valueProducer


import scala.collection.immutable.List


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

  def findProduct(attribute: String, filter: String) = Action.async {
    Logger.info("ProductController findProduct attribute = " + attribute + " filter = " + filter)
    val product: Future[List[Product]] = productCollection.find(Json.obj(attribute -> filter)).cursor[Product].collect[List]()
    product.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def getProductIds(lOfUserToProducts: List[UserToProduct]): ListBuffer[String] = {
    Logger.info("ProductController getProductIds")
    var lOfProductIds = ListBuffer [String]()
    for(userToProduct <- lOfUserToProducts) {
      Logger.info(userToProduct.userId + " - " + userToProduct.productId)
      lOfProductIds += userToProduct.productId
    }
    lOfProductIds
  }

  def getFindMyProductsJasonString (lOfProducts: ListBuffer[String]): String = {
    Logger.info("ProductController getJasonString")
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
    Logger.info("ProductController findMyProducts userId " + userId)
    var lOfProducts = ListBuffer [String]()
    for {
      lOfUserToProducts <- userToProductCollection.find(Json.obj("userId" -> userId)).cursor[UserToProduct].collect[List]()
      lOfProducts = getProductIds(lOfUserToProducts)
      jsonString = getFindMyProductsJasonString (lOfProducts)
      json = play.api.libs.json.Json.parse(jsonString)
      lOfProducts <- productCollection.find(json).cursor[Product].collect[List]()
    } yield {
      Logger.info(""+lOfProducts.size)
      for (product <- lOfProducts) println(product.toString)
      Ok(Json.toJson(lOfProducts))
    }
  }

  def listProducts = Action.async {
    Logger.info("ProductController listProducts")
    val products: Future[List[Product]] = productCollection.genericQueryBuilder.cursor[Product].collect[List]()
    products.map { result =>
      Ok(views.html.listproducts("List of All products")((result)))
    }
  }

  def listJsonProducts = Action.async {
    Logger.info("ProductController listJsonProducts ")
    val products: Future[List[Product]] = productCollection.genericQueryBuilder.cursor[Product].collect[List]()
    products.map { result =>
      Ok(Json.toJson(result))
    }
  }

  def getSearchParameterMap(author: String, title: String, productId: String, manufacturer: String): mutable.Map[String, String] = {
    Logger.info("ProductController getSearchParamaterMap")
    var index = 0
    val parameterMap : mutable.Map[String, String] = mutable.Map()
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
    parameterMap
  }

  def getJsonSearchString (parameterMap: mutable.Map[String, String]) = {
    Logger.info("ProductController getJsonSearchString")
    var jsonString = "{ "
    var index = 0
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
    jsonString
  }

  def searchCatalogue (author: String, title: String, productId: String, manufacturer: String)
    =  Action.async {
    Logger.info("ProductController  searchCatalogue")
    val parameterMap = getSearchParameterMap(author, title, productId, manufacturer)
    val json = play.api.libs.json.Json.parse(getJsonSearchString (parameterMap))
    val products: Future[List[Product]] =
      productCollection.find(json).cursor[Product].collect[List]()
    products.map { result =>
      println(Json.toJson(result))
      Ok(Json.toJson(result))
    }
  }

  def addProduct (author: String, title: String, productid: String, manufacturer: String,
                  productgroup: String, productidtype: String, productIndex: String, imageURL: String,
                  imageLargeURL: String, source: String, sourceid: String)
    = {
    Logger.info("ProductController  addProduct")
    val product = new Product (BSONObjectID.generate,
      author,
      imageURL,
      imageLargeURL,
      manufacturer,
      productIndex,
      productgroup,
      productid,
      productidtype,
      source,
      sourceid,
      title,
      new DateTime(),
      new DateTime())
    Logger.info(product.toString)
    val futureUpdateProduct = productCollection.insert(product)
    futureUpdateProduct.map { result =>
      Logger.info("success " + product.productId + " has been created")
      Logger.info("**** " + result + "*****")
    }.recover {
      case e => Logger.info(e.getMessage())
    }
    listProducts
  }

  def reloadTestData = {
    Logger.info("ProductController reloadTestData")
    productCollection.drop()
    val src = Source.fromFile(".\\resources\\products.txt").getLines
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

  def backUpTestData = {
    Logger.info("ProductController backUpTestData")
    import java.io._
    val file = new File(".\\\\resources\\\\products.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    for {
      lOfProducts <- productCollection.genericQueryBuilder.cursor[Product].collect[List]()
    } yield {
      for (product <- lOfProducts) {
        val rec = product.delimit + "\n"
        bw.write(rec)
      }
      bw.close()
    }
    listProducts
  }

}
