package org.mqjd.flink.jobs.chapter2.section1;

import org.junit.BeforeClass;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.containers.ContainerType;

public class KafkaTestTest extends ContainerBaseTest {
    @BeforeClass
    public static void startContainers() {
        ContainerBaseTest.startContainers(ContainerType.KAFKA);
    }
}