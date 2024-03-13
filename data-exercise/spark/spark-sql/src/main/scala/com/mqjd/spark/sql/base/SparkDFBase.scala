package com.mqjd.spark.sql.base

import org.apache.spark.sql.SparkSession

class SparkDFBase {
  def createSparkSession(name: String = "Hello SparkDF"): SparkSession = {
    SparkSession.builder()
      .master("local")
      .appName(name)
      .getOrCreate()
  }

}