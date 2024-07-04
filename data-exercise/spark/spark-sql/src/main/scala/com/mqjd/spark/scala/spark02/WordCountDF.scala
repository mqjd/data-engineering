package com.mqjd.spark.scala.spark02

import com.mqjd.spark.scala.base.SparkDFBase
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.desc
import org.apache.spark.sql.functions.explode
import org.apache.spark.sql.functions.split
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession

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
