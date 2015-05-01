package controllers.product

import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._

import controllers.authentication.RegisteredUserController
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import play.api.test.{FakeRequest, FakeApplication, WithApplication}

/**
 * Created by david.a.brayfield on 09/04/2015.
 */
class ProductControllerSpec extends FlatSpec with Matchers with BeforeAndAfter {

  println("Starting tests")
  println()

  "A user to product " should "be returned " in {
    val userToProduct = ProductController.setUpUserToProduct
    userToProduct.userId should be ("userid8")
    println("userToProduct.crDate       - " + userToProduct.crDate)
    println("userToProduct.productId    - " + userToProduct.productId)
    println("userToProduct.productIndex - " + userToProduct.productIndex)
    println("userToProduct.updDate      - " + userToProduct.updDate)
    println("userToProduct.userId       - " + userToProduct.userId)
  }

  "A product " should "be returned " in {
    val product = ProductController.setUpProduct
    product.author should be ("Fleming")
    println("product.author        - " + product.author)
    println("product.crDate        - " + product.crDate)
    println("product.imageLargeURL - " + product.imageLargeURL)
    println("product.imageURL      - " + product.imageURL)
    println("product.manufacturer  - " + product.manufacturer)
    println("product.productgroup  - " + product.productgroup)
    println("product.productId     - " + product.productId)
    println("product.productidtype - " + product.productidtype)
    println("product.productIndex  - " + product.productIndex)
    println("product.source        - " + product.source)
    println("product.sourceid      - " + product.sourceid)
    println("product.product       - " + product.title)
    println("product.updDate       - " + product.updDate)

  }

  it should "Try and call the method" in new WithApplication(new FakeApplication()) {

    val result = Await.result(ProductController.saveProduct(FakeRequest()), 10 seconds)
    println("** " + result)

  }
}
