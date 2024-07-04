package org.mqjd.spark.java;

import static org.junit.Assert.assertEquals;

import java.util.Objects;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

public class SparkJobTest {

    protected static SparkSession getSparkSession() {
        return SparkSession.builder().master("local").appName("spark test").getOrCreate();
    }

    protected static String getResourceFile(String filePath) {
        return Objects.requireNonNull(SparkJobTest.class.getResource(filePath)).getFile();
    }

    protected static void assertDatasetEquals(Dataset<?> expect, Dataset<?> actual) {
        assertEquals(expect.schema(), actual.schema());
        assertEquals(expect.collectAsList(), actual.collectAsList());
    }

}
