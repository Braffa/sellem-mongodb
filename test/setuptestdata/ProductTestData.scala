package setuptestdata

import scala.io.Source


/**
 * Created by david.a.brayfield on 01/05/2015.
 */
object ProductTestData extends App {

    println("fileRead")

    val src2 = Source.fromFile(".\\resources\\products")
    val iter = src2.getLines()
    iter.foreach(a => println(a))

    val src = Source.fromFile(".\\resources\\products").getLines
    val headerLine = src.take(1).next
    println(src)
  // processing remaining lines
  for(l <- src) {
    // split line by comma and process them
    l.split(", ").map { c =>
      println(c)
    }

  }

  }

