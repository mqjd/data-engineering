package com.mqjd.spark.scala.spark01

import com.mqjd.spark.scala.SparkTest
import org.scalatest.funsuite.AnyFunSuite

class HelloSparkDFTest extends AnyFunSuite with SparkTest {

  test("given expect dataset when HelloSparkDF then success") {
    val targetDir = getTargetFile("spark01")
    deleteDirectory(targetDir)
    val args = Array("--output", targetDir)
    HelloSparkDF.main(args)
    val expectResult =
      spark.read.option("delimiter", ",").option("header", "true").csv(targetDir).toDF()
    val actualResult =
      spark.read.option("delimiter", ",").option("header", "true").csv(getResourceFile("/spark01.csv")).toDF()
    assertDataFrameEquals(expectResult, actualResult)
  }
}
