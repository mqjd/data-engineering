package org.mqjd.spark.java.spark01;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import org.mqjd.spark.java.SparkJobTest;

public class HelloSparkDFTest extends SparkJobTest {

    @Test
    public void given_expect_dataset_when_HelloSparkDF_then_success() {
        String targetDir = "target/spark01";
        HelloSparkDF.main(new String[]{"--output", targetDir});
        SparkSession spark = getSparkSession();
        Dataset<Row> expectResult = spark.read().option("delimiter", ",").option("header", "true")
            .csv(targetDir).toDF();
        Dataset<Row> actualResult = spark.read().option("delimiter", ",").option("header", "true")
            .csv(getResourceFile("/spark01.csv")).toDF();
        assertDatasetEquals(expectResult, actualResult);
    }

    @Test
    public void test() {
        SparkSession spark = getSparkSession();
        Dataset<Row> actualResult = spark.read().option("delimiter", ",").option("header", "true")
            .csv(getResourceFile("/spark01.csv")).toDF();
        Dataset<Row> df = actualResult.select(new Column("number").cast("long"));
        df = df.select(new Column("number").plus(2L).multiply(10).divide(3));
        df.printSchema();
        df.explain(true);
    }

}
