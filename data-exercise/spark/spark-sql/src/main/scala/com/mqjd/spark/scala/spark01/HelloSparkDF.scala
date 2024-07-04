package com.mqjd.spark.scala.spark01

import com.mqjd.spark.scala.base.ParameterTool
import com.mqjd.spark.scala.base.SparkDFBase
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.SparkSession

object HelloSparkDF extends SparkDFBase {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = createSparkSession()
    val parameterTool = ParameterTool.fromArgs(args)
    val output = parameterTool.get(ParameterTool.OUTPUT)
    val df = spark.range(1, 10).toDF("number")
    val mapped_df = df.select((df.col("number") + 10).alias("number"))
    mapped_df.write.option("delimiter", ",").option("header", "true").mode(SaveMode.Overwrite).csv(output)
    spark.stop()
  }
}
