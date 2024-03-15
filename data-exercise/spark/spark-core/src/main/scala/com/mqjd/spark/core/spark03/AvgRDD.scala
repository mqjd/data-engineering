package com.mqjd.spark.core.spark03

import com.mqjd.spark.core.spark01.HelloSparkRDD.createSparkContext
import org.apache.spark.rdd.RDD

object AvgRDD {
  def main(args: Array[String]): Unit = {
    val spark = createSparkContext()
    val nums: RDD[Int] = spark.parallelize(List(1, 2, 3, 4))
    val result: (Int, Int) =
      nums.aggregate((0, 0))((x, y) => (x._1 + y, x._2 + 1), (x, y) => (x._1 + y._1, x._2 + y._2))
    val avg: Float = result._1 / result._2.toFloat
    println(avg)
    spark.stop()
  }
}
