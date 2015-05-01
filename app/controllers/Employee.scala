/*
 * https://github.com/amitdev/simple-rest-scala/blob/master/tutorial/index.html
 */

package controllers

import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._
import play.api.Logger

import scala.concurrent.Future
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.bson.BSONObjectID

import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper

import java.util.concurrent.TimeoutException

import models.Employee
import models.JsonFormats.employeeFormat


object Employee extends Controller with MongoController {
  
  def collection: JSONCollection = db.collection[JSONCollection]("employees")

  def listMyEmployees = Action.async {
    val emps: Future[List[Employee]] = collection.genericQueryBuilder.cursor[Employee].collect[List]()
    
    emps.map { result => 
    System.out.println("*******************")
    System.out.println(result)
    System.out.println("*******************")
    result.foreach { x => System.out.println(x._id) 
                          System.out.println(x.name)
                          System.out.println(x.dob)
                          System.out.println(x.joiningDate) }
    System.out.println("*******************")
    }
    emps.map { result =>
      Ok(Json.toJson(result))
    }
  }
  def findEmployee = Action.async {
    var filter =  "Deepa"
    val emps: Future[List[Employee]] = collection.find(Json.obj("name" -> filter)).cursor[Employee].collect[List]()
    
    emps.map { result => 
    System.out.println("*******************")
    System.out.println(result)
    System.out.println("*******************")
    result.foreach { x => System.out.println(x._id) 
                          System.out.println(x.name)
                          System.out.println(x.dob)
                          System.out.println(x.joiningDate) }
    System.out.println("*******************")
    }
    emps.map { result =>
      Ok(Json.toJson(result))
    }
  }
    
 def saveEmployee = Action {
   val employee = new Employee(BSONObjectID.generate, "Randy", "13 High Street", new java.util.Date(), new java.util.Date(), "Shop Floor")
   //val futureUpdateEmp = collection.insert(employee.copy(_id = BSONObjectID.generate))
   
   val futureUpdateEmp = collection.insert(employee)
   
   futureUpdateEmp.map { result =>
          System.out.println("success " + employee.name + " has been created")}
   Ok("Got Here " + employee.name)
  }
 
  def deleteEmployee() = Action {
    val id = "5523bdaa51737b77048fbd50"
    val futureInt = collection.remove(Json.obj("_id" -> Json.obj("$oid" -> id)), firstMatchOnly = true)
    Ok("deleted ")
  }
  def deleteEmployeeByName() = Action {
    val name = "John"
    val futureInt = collection.remove(Json.obj("name" -> name), firstMatchOnly = true)
    Ok("deleted ")
  }
}
