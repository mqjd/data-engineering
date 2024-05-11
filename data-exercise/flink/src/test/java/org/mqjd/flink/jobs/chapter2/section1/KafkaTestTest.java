package org.mqjd.flink.jobs.chapter2.section1;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.containers.ContainerType;
import org.testcontainers.containers.KafkaContainer;

public class KafkaTestTest extends ContainerBaseTest {

    @BeforeClass
    public static void startContainers() {
        ContainerBaseTest.startContainers(ContainerType.KAFKA);
    }

    @Test
    public void given_correct_input_and_output_when_KafkaTest_then_success() throws Exception {
        KafkaContainer container = getContainer(ContainerType.KAFKA);
        String bootstrapServers = container.getBootstrapServers();
        String[] params = {"-D", STR."source.property.bootstrap.servers=\{bootstrapServers}"};
        KafkaTest.main(params);
    }
}