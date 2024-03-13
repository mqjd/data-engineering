package com.mqjd.spark.core.spark01

import com.mqjd.spark.core.base.SparkRDDBase
import org.apache.spark.{SparkConf, SparkContext}

object HelloSparkRDD extends SparkRDDBase{
  def main(args: Array[String]): Unit = {
    val spark = createSparkContext()
    val lines = spark.parallelize(Seq("hello world", "hello tencent"))
    val wc = lines.flatMap(_.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
    wc.foreach(println)
    spark.stop()
  }

}
