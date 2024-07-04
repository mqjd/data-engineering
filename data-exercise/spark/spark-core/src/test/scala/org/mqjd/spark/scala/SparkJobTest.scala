package org.mqjd.spark.scala

import org.apache.commons.io.FileUtils
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import java.io.File

trait SparkJobTest {
  lazy val spark: SparkContext = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
    new SparkContext(sparkConf)
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

  def assertRddEquals(expect: RDD[_], actual: RDD[_]): Unit = {
    assert(expect.collect() sameElements actual.collect())
  }
}
