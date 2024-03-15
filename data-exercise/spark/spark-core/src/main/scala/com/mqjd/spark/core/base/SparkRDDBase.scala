package com.mqjd.spark.core.base

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

class SparkRDDBase {
  def createSparkContext(name: String = "Hello SparkRDD"): SparkContext = {
    val sparkConf = new SparkConf().setMaster("local").setAppName(name)
    new SparkContext(sparkConf)
  }
}
