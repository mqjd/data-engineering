package com.mqjd.spark.sql.spark11

import com.mqjd.spark.sql.base.SparkDFBase
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

object FinalChargeTimeDF extends SparkDFBase {
  private val secondsInOneHour = 3600

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val input = createDataFrame(spark)
    val startTransactionRequestsDF = input.transform(filterStartTransactionRequests).transform(convertStartTransactionRequestBody)
    val startTransactionResponsesDF = input.transform(filterStartTransactionResponses).transform(convertStartTransactionResponseBody)
    val startTransactionsDF = joinStartTransactions(startTransactionRequestsDF, startTransactionResponsesDF)
    val stopTransactionRequestsDF = input.transform(filterStopTransactionRequests).transform(convertStopTransactionRequestBody)
    val calculateTotalDF = joinStartStopTransactions(startTransactionsDF, stopTransactionRequestsDF).transform(calculateTotalTimeHours).transform(calculateTotalEnergy)
    val meterValuesDF = input.transform(filterMeterValues).transform(convertMeterValueBody).transform(reshapeMeterValues).transform(calculateTotalParkingTimeHours)
    joinTotalParking(calculateTotalDF, meterValuesDF).show()
    spark.stop()
  }

  private def createDataFrame(spark: SparkSession): DataFrame = {
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
      .load("assets/spark/charging-data.csv.gz")
  }

  private def filterStopTransactionRequests(input: DataFrame): DataFrame = {
    input.filter(
      input.col("action").equalTo("StopTransaction")
        .&&(input.col("message_type").equalTo(2))
    )
  }

  private def convertStopTransactionRequestBody(input: DataFrame): DataFrame = {
    val bodySchema = StructType(
      Seq(
        StructField("meter_stop", IntegerType, nullable = true),
        StructField("timestamp", StringType, nullable = true),
        StructField("transaction_id", IntegerType, nullable = true),
        StructField("reason", StringType, nullable = true),
        StructField("id_tag", StringType, nullable = true),
        StructField("transaction_data", ArrayType(StringType), nullable = true),
      )
    )
    input.withColumn("new_body", from_json(input.col("body"), bodySchema))
  }


  private def filterStartTransactionRequests(input: DataFrame): DataFrame = {
    input.filter(
      input.col("action").equalTo("StartTransaction")
        .&&(input.col("message_type").equalTo(2))
    )
  }

  private def convertStartTransactionRequestBody(input: DataFrame): DataFrame = {
    val bodySchema = StructType(
      Seq(
        StructField("connector_id", IntegerType, nullable = true),
        StructField("id_tag", StringType, nullable = true),
        StructField("meter_start", IntegerType, nullable = true),
        StructField("timestamp", StringType, nullable = true),
        StructField("reservation_id", StringType, nullable = true),
      )
    )
    input.withColumn("new_body", from_json(input.col("body"), bodySchema))
  }

  private def filterStartTransactionResponses(input: DataFrame): DataFrame = {
    input.filter(
      input.col("action").equalTo("StartTransaction")
        .&&(input.col("message_type").equalTo(3))
    )
  }

  private def convertStartTransactionResponseBody(input: DataFrame): DataFrame = {
    val bodySchema = StructType(
      Seq(
        StructField("transaction_id", IntegerType, nullable = true),
        StructField("id_tag_info", StructType(
          Seq(
            StructField("status", StringType, nullable = true),
            StructField("parent_id_tag", StringType, nullable = true),
            StructField("expiry_date", StringType, nullable = true),
          )
        ), nullable = true),
      )
    )
    input.withColumn("new_body", from_json(input.col("body"), bodySchema))
  }

  private def joinStartTransactions(requestDF: DataFrame, responseDF: DataFrame): DataFrame = {
    requestDF.as("request_df").join(responseDF.as("response_df"), Seq("message_id"))
      .select(
        col("response_df.charge_point_id"),
        col("response_df.new_body.transaction_id"),
        col("request_df.new_body.meter_start"),
        col("request_df.new_body.timestamp").alias("start_timestamp")
      )
  }

  private def joinStartStopTransactions(startDF: DataFrame, stopDF: DataFrame): DataFrame = {
    stopDF.as("stop_df")
      .join(startDF.as("start_df"), col("stop_df.new_body.transaction_id") === col("start_df.transaction_id"), "left")
      .select(
        col("start_df.charge_point_id"),
        col("start_df.transaction_id"),
        col("start_df.meter_start"),
        to_timestamp(col("start_df.start_timestamp")).alias("start_timestamp"),
        col("stop_df.new_body.meter_stop").alias("meter_stop"),
        to_timestamp(col("stop_df.new_body.timestamp")).alias("stop_timestamp"),
      )
  }

  private def calculateTotalTimeHours(inputDF: DataFrame): DataFrame = {
    inputDF.withColumn("total_time", col("stop_timestamp").cast("long") / secondsInOneHour - col("start_timestamp").cast("long") / secondsInOneHour)
      .withColumn("total_time", round(col("total_time").cast(DoubleType), 2))
  }

  private def calculateTotalEnergy(inputDF: DataFrame): DataFrame = {
    inputDF.withColumn("total_energy", col("meter_stop") - col("meter_start"))
      .withColumn("total_energy", round(col("total_energy").cast(DoubleType), 2))
  }

  private def filterMeterValues(input: DataFrame): DataFrame = {
    input.filter(
      input.col("action").equalTo("MeterValues")
        .&&(input.col("message_type").equalTo(2))
    )
  }

  private def convertMeterValueBody(input: DataFrame): DataFrame = {
    val bodySchema = StructType(
      Seq(
        StructField("connector_id", IntegerType, nullable = true),
        StructField("transaction_id", IntegerType, nullable = true),
        StructField("meter_value", ArrayType(
          StructType(
            Seq(
              StructField("timestamp", StringType, nullable = true),
              StructField("sampled_value", ArrayType(
                StructType(
                  Seq(
                    StructField("value", StringType, nullable = true),
                    StructField("context", StringType, nullable = true),
                    StructField("format", StringType, nullable = true),
                    StructField("measurand", StringType, nullable = true),
                    StructField("phase", StringType, nullable = true),
                    StructField("unit", StringType, nullable = true),
                  )
                )
              ), nullable = true),
            )
          )
        ), nullable = true),
      )
    )
    input.withColumn("new_body", from_json(input.col("body"), bodySchema))
  }

  private def reshapeMeterValues(input: DataFrame): DataFrame = {
    input.withColumn("meter_value", explode(col("new_body.meter_value")))
      .withColumn("sampled_value", explode(col("meter_value.sampled_value")))
      .select(
        to_timestamp(col("meter_value.timestamp")).alias("timestamp"),
        col("sampled_value.measurand"),
        col("sampled_value.phase"),
        round(col("sampled_value.value").cast(DoubleType), 2).alias("value"),
        col("new_body.transaction_id"),
      )
      .filter((col("measurand") === "Power.Active.Import") && col("phase").isNull)
  }

  private def calculateTotalParkingTimeHours(inputDF: DataFrame): DataFrame = {
    val window = Window.partitionBy("transaction_id").orderBy(col("timestamp").asc)
    inputDF.withColumn("value", when(col("value") > 0, 1).otherwise(0))
      .withColumn("value_changed", abs(col("value") - lag(col("value"), 1, 0).over(window)))
      .withColumn("charging_group", sum("value_changed").over(window))
      .filter(col("value") === 0)
      .groupBy("transaction_id", "charging_group")
      .agg((last("timestamp") - first("timestamp")).cast(LongType).alias("group_duration"))
      .groupBy("transaction_id")
      .agg(round(sum("group_duration") / secondsInOneHour, 2).alias("total_parking_time"))
  }

  private def joinTotalParking(inputDF: DataFrame, joinDF: DataFrame): DataFrame = {
    inputDF.alias("main")
      .join(joinDF.alias("parking"), "transaction_id")
      .drop("parking.transaction_id")
  }

}
