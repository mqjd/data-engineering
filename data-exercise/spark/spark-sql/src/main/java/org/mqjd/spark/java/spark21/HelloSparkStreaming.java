package org.mqjd.spark.java.spark21;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.Trigger;
import org.mqjd.spark.scala.base.SparkDFBase;

public class HelloSparkStreaming extends SparkDFBase {

    public static void main(String[] args) throws Exception {
        try (SparkSession spark = createSession("HelloSparkStreaming")) {
            Dataset<Row> source = spark.readStream().format("rate").option("rowsPerSecond", 10)
                .load();
            StreamingQuery query = source.writeStream().format("console")
                .outputMode(OutputMode.Append()).trigger(Trigger.ProcessingTime(1000)).start();
            query.awaitTermination();
        }
    }

}
