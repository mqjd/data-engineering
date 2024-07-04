package com.mqjd.spark.java;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkJobTest {

    protected static JavaSparkContext getSparkContext() {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("spark test");
        return new JavaSparkContext(sparkConf);
    }

    protected static void deleteDirectory(String file) {
        try {
            FileUtils.deleteDirectory(new File(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String getResourceFile(String filePath) {
        return Objects.requireNonNull(SparkJobTest.class.getResource(filePath)).getFile();
    }

    protected static void assertRddEquals(JavaRDD<?> expect, JavaRDD<?> actual) {
        assertEquals(expect.collect(), actual.collect());
    }
}
