package org.mqjd.spark.scala.spark04

import org.mqjd.spark.scala.base.SparkDFBase
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession

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

    spark.read
      .format("csv")
      .option("header", value = true)
      .option("delimiter", ",")
      .schema(schema)
      .load("assets/spark/charging-data.csv")
  }
}
