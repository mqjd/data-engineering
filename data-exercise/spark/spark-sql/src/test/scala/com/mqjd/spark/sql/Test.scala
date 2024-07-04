package com.mqjd.spark.sql

import com.mqjd.spark.sql.spark01.HelloSparkDF
import org.scalatest.funsuite.AnyFunSuite

class Test extends AnyFunSuite with SparkSessionTestWrapper {
  test("An empty Set should have size 0") {
    val args = Array("--output", "target/spark01")
    HelloSparkDF.main(args)
  }
}
