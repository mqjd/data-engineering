package com.mqjd.spark.sql.spark02

import com.mqjd.spark.sql.base.SparkDFBase
import org.apache.spark.sql.functions.{col, desc, explode, split}
import org.apache.spark.sql.{Dataset, SparkSession}

object WordCountDF extends SparkDFBase {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val linesDF: Dataset[String] = spark.read.textFile("assets/spark/word-count.txt")
    val wordsDF = linesDF.select(explode(split(col("value"), "\\s+")).as("word"))
    val wordCountDF = wordsDF.groupBy("word").count().alias("count").orderBy(desc("count"))
    val rows = wordCountDF.collect()
    rows.foreach(println)
    spark.stop()
  }
}
