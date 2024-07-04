package org.mqjd.spark.scala.base

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.api.java.JavaSparkContext

class SparkRDDBase {
  def sparkContext(name: String = "Hello Spark"): SparkContext = {
    SparkRDDBase.createSparkContext(name)
  }
}

object SparkRDDBase {
  def createSparkContext(name: String = "Hello Spark"): SparkContext = {
    val sparkConf = new SparkConf().setMaster("local").setAppName(name)
    new SparkContext(sparkConf)
  }

  def createJavaSparkContext(name: String = "Hello Spark"): JavaSparkContext = {
    val sparkConf = new SparkConf().setMaster("local").setAppName(name)
    new JavaSparkContext(sparkConf)
  }
}
