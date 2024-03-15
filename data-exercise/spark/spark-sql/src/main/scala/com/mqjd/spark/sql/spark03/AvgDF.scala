package com.mqjd.spark.sql.spark03

import com.mqjd.spark.sql.base.SparkDFBase
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.functions.sum
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession

object AvgDF extends SparkDFBase {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val data = List(Row(1), Row(2), Row(3), Row(4))
    val schema = StructType(Seq(StructField("value", IntegerType)))
    val nums = spark.createDataFrame(spark.sparkContext.parallelize(data), schema)
    val result = nums.agg(sum(col("value")).alias("all"), count(col("value")).alias("count"))
    val rows = result.select(col("all") / col("count").alias("avg")).collect()
    rows.foreach(println)
    spark.stop()
  }
}
