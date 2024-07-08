package org.mqjd.spark.java.spark21;

import java.util.regex.Pattern;
import org.apache.spark.api.java.JavaSparkContext;
import org.mqjd.spark.scala.base.SparkRDDBase;

public class HelloSparkStreaming extends SparkRDDBase {

    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
        JavaSparkContext sc = createJavaSparkContext("HelloSparkRDD");
        sc.close();
    }

}
