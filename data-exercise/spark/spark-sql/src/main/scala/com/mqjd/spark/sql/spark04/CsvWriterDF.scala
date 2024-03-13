package com.mqjd.spark.sql.spark04

import com.mqjd.spark.sql.base.SparkDFBase
import com.mqjd.spark.sql.spark04.CsvReaderDF.readChargingData
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object CsvWriterDF extends SparkDFBase {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val csvDF: DataFrame = readChargingData(spark)
    csvDF.write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv("target/output.csv")
    spark.stop()
  }
}
