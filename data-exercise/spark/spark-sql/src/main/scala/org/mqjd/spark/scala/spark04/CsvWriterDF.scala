package org.mqjd.spark.scala.spark04

import org.mqjd.spark.scala.base.SparkDFBase
import org.mqjd.spark.scala.spark04.CsvReaderDF.readChargingData
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SparkSession

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
