package com.mqjd.spark.scala

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession

trait SparkTest {
  lazy val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local")
      .appName("spark test")
      .config("spark.sql.shuffle.partitions", "1")
      .getOrCreate()
  }

  def getResourceFile(name: String): String = {
    getClass.getResource(name).getFile
  }

  def getTargetFile(name: String): String = {
    new File(getResourceFile("/")).getParentFile.toString + File.separator + name
  }

  def deleteDirectory(file: String): Unit = {
    FileUtils.deleteDirectory(new File(file))
  }

  def assertDataFrameEquals(expect: DataFrame, actual: DataFrame): Unit = {
    assert(expect.schema == actual.schema)
    assert(expect.collectAsList() == actual.collectAsList())
  }
}
