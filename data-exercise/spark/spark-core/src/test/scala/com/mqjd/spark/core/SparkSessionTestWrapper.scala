package com.mqjd.spark.core

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

trait SparkSessionTestWrapper {
  lazy val spark: SparkContext = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
    new SparkContext(sparkConf)
  }
}
