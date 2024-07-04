package com.mqjd.spark.scala.spark02

import com.mqjd.spark.scala.spark01.HelloSparkRDD.sparkContext
import org.apache.spark.rdd.RDD

object WordCountRDD {
  def main(args: Array[String]): Unit = {
    val spark = sparkContext()
    // 内容来自于 https://www.altexsoft.com/blog/big-data-engineer/
    val lines: RDD[String] = spark.textFile("assets/spark/word-count.txt")
    val words = lines.flatMap(v => v.split(" "))
    val wordCount: RDD[(String, Int)] = words.map(x => (x, 1)).reduceByKey(_ + _).sortBy(_._2, ascending = false)
    val result: Array[(String, Int)] = wordCount.collect()
    result.foreach(println)
    spark.stop()
  }
}
