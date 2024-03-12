package com.mqjd

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Test {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("test")
    val spark = new SparkContext(sparkConf)
    val textLines: RDD[String] = spark.textFile("assets/word-count.txt")
    val words = textLines.flatMap(_.split(" "));
    val wordCount = words.groupBy(word => word).map { case (word, list) => (word, list.size) }
    val tuples = wordCount.collect()
    tuples.foreach(println)
    spark.stop()
  }

}
