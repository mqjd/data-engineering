package org.mqjd.flink.jobs.chapter1.section1;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mqjd.flink.jobs.FlinkJobTest;

public class AccumulateSumTest extends FlinkJobTest {

    private static final String WORK_PATH = "/chapter1/section1";
    private static final String OUTPUT_PATH = String.format("target%s/output", WORK_PATH);
    private static final String EXPECTED_PATH = String.format("%s/top-speed.expected.txt", WORK_PATH);

    @Test
    public void given_correct_input_and_output_when_AccumulateSum_then_success() throws Exception {
        FileUtils.deleteDirectory(new File(OUTPUT_PATH));
        String[] params = {"--output", OUTPUT_PATH, "--from", "1", "--to", "100"};
        AccumulateSum.main(params);
        compareResultsByLines(EXPECTED_PATH, OUTPUT_PATH);
    }
}