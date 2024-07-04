package com.mqjd.spark.scala.spark01

import com.mqjd.spark.scala.SparkJobTest
import org.scalatest.funsuite.AnyFunSuite

class HelloSparkRDDTest extends AnyFunSuite with SparkJobTest {
  test("given expect dataset when HelloSparkRDD then success") {
    val targetDir = getTargetFile("spark01")
    deleteDirectory(targetDir)
    val args = Array("--output", targetDir)
    HelloSparkRDD.main(args)
    val actualRdd = spark.textFile(targetDir)
    val expectRdd = spark.textFile(getResourceFile("/spark01.txt"))
    assertRddEquals(actualRdd, expectRdd)
  }
}
