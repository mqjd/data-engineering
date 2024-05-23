package org.mqjd.flink.jobs.chapter3.section1;

import java.io.IOException;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.state.api.SavepointReader;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class StateReaderTest {

    void stateReaderTest() throws IOException {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        SavepointReader savepoint = SavepointReader.read(env, "hdfs://path/",
            new HashMapStateBackend());
    }

}