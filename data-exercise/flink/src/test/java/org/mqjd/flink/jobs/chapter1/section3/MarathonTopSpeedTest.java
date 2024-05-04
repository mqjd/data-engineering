package org.mqjd.flink.jobs.chapter1.section3;

import org.junit.Test;
import org.mqjd.flink.jobs.FlinkJobTest;

public class MarathonTopSpeedTest extends FlinkJobTest {
    private static final String WORK_PATH = "/chapter1/section3";
    public static final String INPUT_PATH = getResourceFile(String.format("%s/user.metric.csv", WORK_PATH));
    private static final String EXPECTED_PATH = String.format("%s/top-speed.expected.txt", WORK_PATH);
    private static final String OUTPUT_PATH = String.format("target%s/output", WORK_PATH);

    @Test
    public void given_correct_input_and_output_when_TopSpeed_then_success() throws Exception {
        deleteDirectory(OUTPUT_PATH);
        String[] params = { "--input", INPUT_PATH, "--output", OUTPUT_PATH };
        MarathonTopSpeed.main(params);
        compareResultsByLines(EXPECTED_PATH, OUTPUT_PATH);
    }
}