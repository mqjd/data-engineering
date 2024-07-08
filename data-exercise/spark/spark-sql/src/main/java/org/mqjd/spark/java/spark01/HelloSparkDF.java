package org.mqjd.spark.java.spark01;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.mqjd.spark.scala.base.ParameterTool;
import org.mqjd.spark.scala.base.SparkDFBase;

public class HelloSparkDF extends SparkDFBase {

    public static void main(String[] args) {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String output = parameterTool.get(ParameterTool.OUTPUT());
        try (SparkSession spark = createSession("HelloSparkDF")) {
            Dataset<Row> source = spark.range(1, 10).toDF("number");
            Dataset<Row> mapped = source.select(source.col("number").plus(10).alias("number"));
            mapped.write().option("delimiter", ",").option("header", "true")
                .mode(SaveMode.Overwrite).csv(output);
        }
    }
}
