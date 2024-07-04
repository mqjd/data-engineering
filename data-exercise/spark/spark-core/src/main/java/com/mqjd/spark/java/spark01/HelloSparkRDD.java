package com.mqjd.spark.java.spark01;

import com.mqjd.spark.scala.base.ParameterTool;
import com.mqjd.spark.scala.base.SparkRDDBase;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class HelloSparkRDD extends SparkRDDBase {

    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) {
        JavaSparkContext sc = createJavaSparkContext("HelloSparkRDD");
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String outputPath = parameterTool.get(ParameterTool.OUTPUT());
        List<String> data = Arrays.asList("hello world", "hello spark");
        sc.parallelize(data).flatMap(s -> Arrays.asList(SPACE.split(s)).iterator())
            .mapToPair(s -> new Tuple2<>(s, 1)).reduceByKey(Integer::sum)
            .saveAsTextFile(outputPath);
        sc.close();
    }

}
