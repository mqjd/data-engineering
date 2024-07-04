package org.mqjd.spark.scala.spark03

import org.mqjd.spark.scala.spark01.HelloSparkRDD.sparkContext
import org.apache.spark.rdd.RDD

object AvgRDD {
  def main(args: Array[String]): Unit = {
    val spark = sparkContext()
    val nums: RDD[Int] = spark.parallelize(List(1, 2, 3, 4))
    val result: (Int, Int) =
      nums.aggregate((0, 0))((x, y) => (x._1 + y, x._2 + 1), (x, y) => (x._1 + y._1, x._2 + y._2))
    val avg: Float = result._1 / result._2.toFloat
    println(avg)
    spark.stop()
  }
}
