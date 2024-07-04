package org.mqjd.spark.scala.base

import scala.collection.mutable

import com.google.common.base.Preconditions
import org.apache.commons.lang3.math.NumberUtils

class ParameterTool(map: mutable.Map[String, List[String]]) { self =>
  def get(key: String): String = {
    if (map.contains(key)) {
      val value = map(key)
      Preconditions.checkState(value.size == 1, "Key %s should has only one value.", key)
      value.last
    } else {
      null
    }
  }
}

object ParameterTool {
  val INPUT = "input"
  val OUTPUT = "output"
  private val NO_VALUE_KEY = "__NO_VALUE_KEY"
  def fromArgs(args: Array[String]): ParameterTool = {
    val map: mutable.Map[String, List[String]] = mutable.Map[String, List[String]]()
    var i = 0
    while (i < args.length) {
      val key = getKeyFromArgs(args, i)
      i += 1 // try to find the value
      map.getOrElseUpdate(key, List[String]())
      if (i >= args.length)
        map.update(key, map(key) :+ NO_VALUE_KEY)
      else if (NumberUtils.isCreatable(args(i))) {
        map.update(key, map(key) :+ args(i))
        i += 1
      } else if (args(i).startsWith("--") || args(i).startsWith("-")) {
        map.update(key, map(key) :+ NO_VALUE_KEY)
      } else {
        map.update(key, map(key) :+ args(i))
        i += 1
      }
    }
    new ParameterTool(map)
  }

  private def getKeyFromArgs(args: Array[String], index: Int): String = {
    var key: String = null
    if (args(index).startsWith("--")) {
      key = args(index).substring(2)
    } else {
      if (args(index).startsWith("-")) {
        key = args(index).substring(1)
      } else {
        throw new IllegalArgumentException(
          String.format(
            "Error parsing arguments '%s' on '%s'. Please prefix keys with -- or -.",
            args.mkString("Array(", ", ", ")"),
            args(index)
          )
        )
      }
    }
    if (key.isEmpty) {
      throw new IllegalArgumentException(
        "The input " + args.mkString("Array(", ", ", ")") + " contains an empty argument"
      )
    }
    key
  }
}
