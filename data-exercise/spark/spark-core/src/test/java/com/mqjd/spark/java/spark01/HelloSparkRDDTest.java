package com.mqjd.spark.java.spark01;

import com.mqjd.spark.java.SparkJobTest;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

public class HelloSparkRDDTest extends SparkJobTest {

    @Test
    public void given_expect_text_when_HelloSparkRDD_then_success() {
        String targetDir = "target/spark01";
        deleteDirectory(targetDir);
        HelloSparkRDD.main(new String[]{"--output", targetDir});
        JavaSparkContext sc = getSparkContext();
        JavaRDD<String> actual = sc.textFile(targetDir);
        JavaRDD<String> expect = sc.textFile(getResourceFile("/spark01.txt"));
        assertRddEquals(expect, actual);
    }

}