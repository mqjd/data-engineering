package org.mqjd.spark.spark01;

import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class HelloSparkDF {

    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().master("local").appName("HelloSparkDF")
            .getOrCreate();
        Dataset<Row> df = spark.range(1, 10).toDF("number");
        Dataset<Row> mapped_df = df.select(df.col("number").plus(10));
        List<Row> rows = mapped_df.collectAsList();
        rows.forEach(System.out::println);
        spark.stop();
    }

}
