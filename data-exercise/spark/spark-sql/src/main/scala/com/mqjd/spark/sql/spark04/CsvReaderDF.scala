package com.mqjd.spark.sql.spark04

import com.mqjd.spark.sql.base.SparkDFBase
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

object CsvReaderDF extends SparkDFBase {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val csvDF: DataFrame = readChargingData(spark)
    println(csvDF.count())
    spark.stop()
  }

  def readChargingData(spark: SparkSession): DataFrame = {
    val schema = StructType(
      Seq(
        StructField("message_id", StringType, nullable = true),
        StructField("message_type", IntegerType, nullable = true),
        StructField("charge_point_id", StringType, nullable = true),
        StructField("action", StringType, nullable = true),
        StructField("write_timestamp", StringType, nullable = true),
        StructField("body", StringType, nullable = true)
      )
    )

    spark.read.format("csv")
      .option("header", value = true)
      .option("delimiter", ",")
      .schema(schema)
      .load("assets/spark/charging-data.csv")
  }
}
