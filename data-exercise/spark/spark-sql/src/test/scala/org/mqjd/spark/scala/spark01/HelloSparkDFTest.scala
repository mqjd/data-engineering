package org.mqjd.spark.scala.spark01

import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}
import org.mqjd.spark.scala.SparkTest
import org.scalatest.funsuite.AnyFunSuite

class HelloSparkDFTest extends AnyFunSuite with SparkTest {
  test("xxx") {
    // Define schema
    val schema = StructType(Array(
      StructField("id", IntegerType, nullable = true),
      StructField("name", StringType, nullable = true),
      StructField("mount", DoubleType, nullable = true)
    ))
    // Define row data
    val data = Seq(
      Row(1, null, 622543.52127),
      Row(2, " \t asd   ", 622872.91534)
    )
    spark.createDataFrame(spark.sparkContext.parallelize(data), schema).createOrReplaceTempView("test")
    spark.sql("select regexp_replace(name, '[\\s\\t]+', '' ) from test").withColumn("a", lit("asdad")).show()
    println(" \t asd   ")
  }
  test("given expect dataset when HelloSparkDF then success") {
    val targetDir = getTargetFile("spark01")
    deleteDirectory(targetDir)
    val args = Array("--output", targetDir)
    HelloSparkDF.main(args)
    val expectResult =
      spark.read.option("delimiter", ",").option("header", "true").csv(targetDir).toDF()
    val actualResult =
      spark.read.option("delimiter", ",").option("header", "true").csv(getResourceFile("/spark01.csv")).toDF()
    assertDataFrameEquals(expectResult, actualResult)
  }
}
