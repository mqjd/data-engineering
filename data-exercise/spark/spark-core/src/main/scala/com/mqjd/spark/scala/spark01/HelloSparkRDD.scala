package com.mqjd.spark.scala.spark01

import com.mqjd.spark.scala.base.ParameterTool
import com.mqjd.spark.scala.base.SparkRDDBase

object HelloSparkRDD extends SparkRDDBase {
  def main(args: Array[String]): Unit = {
    val spark = sparkContext()
    val parameterTool = ParameterTool.fromArgs(args)
    val output = parameterTool.get(ParameterTool.OUTPUT)
    val lines = spark.parallelize(Seq("hello world", "hello spark"))
    val wc = lines.flatMap(_.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
    wc.saveAsTextFile(output)
    spark.stop()
  }
}
