package com.mqjd.spark.sql.spark01

import com.mqjd.spark.sql.base.SparkDFBase
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SparkSession

object HelloSparkDF extends SparkDFBase {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val df: DataFrame = spark.range(1, 10).toDF("number")
    val mapped_df = df.select(df.col("number") + 10)
    mapped_df.write.option("delimiter", ",").option("header", "true").mode(SaveMode.Overwrite).csv("spark01")
    spark.stop()
  }
}
