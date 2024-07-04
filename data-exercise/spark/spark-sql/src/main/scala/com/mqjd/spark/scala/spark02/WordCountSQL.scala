package com.mqjd.spark.scala.spark02

import com.mqjd.spark.scala.base.SparkDFBase
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession

object WordCountSQL extends SparkDFBase {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession("SparkSQL")
    val linesDF: Dataset[String] = spark.read.textFile("assets/spark/word-count.txt")
    linesDF.createOrReplaceTempView(viewName = "lines")
    spark.sql("select split(value, ' ') as words from lines").createOrReplaceTempView("line_words")
    spark.sql("select explode(words) as word from line_words").createOrReplaceTempView("words")
    val rows = spark
      .sql("""
        select
          count(1) as word_count,
          word
         from words
        group by word
        order by word_count desc""")
      .collect()
    rows.foreach(println)
    spark.stop()
  }
}
