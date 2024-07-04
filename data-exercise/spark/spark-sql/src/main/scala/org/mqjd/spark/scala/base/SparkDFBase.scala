package org.mqjd.spark.scala.base

import org.apache.spark.sql.SparkSession

class SparkDFBase {
  def createSparkSession(name: String = "Hello SparkDF"): SparkSession = {
    SparkDFBase.createSession(name)
  }
}

object SparkDFBase {
  def createSession(name: String = "Hello SparkDF"): SparkSession = {
    SparkSession
      .builder()
      .master("local")
      .appName(name)
      .getOrCreate()
  }
}
