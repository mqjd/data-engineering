package com.mqjd.spark.spark01;

import org.junit.Test;

public class HelloSparkDFTest {

    @Test
    public void testSpark() {
        HelloSparkDF.main(new String[]{"--output", "target/spark01"});
    }
}
