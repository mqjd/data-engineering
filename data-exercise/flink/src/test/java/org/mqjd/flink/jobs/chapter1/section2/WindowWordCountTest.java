package org.mqjd.flink.jobs.chapter1.section2;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.flink.test.util.TestBaseUtils;
import org.junit.Test;
import org.mqjd.flink.jobs.FlinkJobTest;

public class WindowWordCountTest extends FlinkJobTest {
    private static final String INPUT_PATH = getResourceFile("/chapter1/section1/word-count.input.txt");
    private static final String EXPECTED_PATH = "/chapter1/section2/word-count.expected.txt";
    private static final String OUTPUT_PATH = "target/chapter1/section2/output";

    @Test
    public void given_correct_input_and_output_when_WindowWordCount_then_success() throws Exception {
        FileUtils.deleteDirectory(new File(OUTPUT_PATH));
        String[] params = { "--input", INPUT_PATH, "--output", OUTPUT_PATH };
        WindowWordCount.main(params);
        String expectedString =
            FileUtils.readFileToString(new File(getResourceFile(EXPECTED_PATH)), StandardCharsets.UTF_8);
        ArrayList<String> list = new ArrayList<>();
        TestBaseUtils.readAllResultLines(list, new File(OUTPUT_PATH).toURI().toString());
        String[] actual = list.toArray(new String[0]);
        Arrays.sort(actual);
        String[] expected = expectedString.isEmpty() ? new String[0] : expectedString.split(System.lineSeparator());
        Arrays.sort(expected);
        assertArrayEquals(expected, actual);
    }
}