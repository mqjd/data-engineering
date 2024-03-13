package com.mqjd.spark02

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("Hello World")
    val spark: SparkContext = new SparkContext(sparkConf)
    // 内容来自于 https://www.altexsoft.com/blog/big-data-engineer/
    val lines: RDD[String] = spark.textFile("assets/spark/word-count.txt")
    val words = lines.flatMap(v => v.split(" "))
    val wordCount: RDD[(String, Int)] = words.map(x => (x, 1)).reduceByKey(_ + _)
    val result: Array[(String, Int)] = wordCount.collect()
    result.foreach(println)
    spark.stop()
  }
}
