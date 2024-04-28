package org.mqjd.flink.jobs.chapter1.section1;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.flink.test.util.TestBaseUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mqjd.flink.jobs.FlinkJobTest;

public class WordCountTest extends FlinkJobTest {
    private static final String INPUT_PATH = getResourceFile("/chapter1/section1/word-count.input.txt");
    private static final String EXPECTED_PATH = "/chapter1/section1/word-count.expected.txt";
    private static final String OUTPUT_PATH = "target/chapter1/section1/output";

    @Test
    public void given_correct_input_and_output_when_WordCount_then_success() throws Exception {
        FileUtils.deleteDirectory(new File(OUTPUT_PATH));
        String[] params = { "--input", INPUT_PATH, "--output", OUTPUT_PATH };
        WordCount.main(params);
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

    static Stream<Arguments> given_incorrect_input_when_WordCount_then_exception_parameters() {
        return Stream.of(Arguments.arguments("empty args", new String[] { "--input", "" }, RuntimeException.class),
            Arguments.arguments("empty input", new String[] { "--input", "" }, IllegalArgumentException.class),
            Arguments.arguments("not exist input", new String[] { "--input", "/not exist path" },
                RuntimeException.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("given_incorrect_input_when_WordCount_then_exception_parameters")
    public void given_incorrect_input_when_WordCount_then_exception(String name, String[] params,
        Class<Exception> clz) {
        Assert.assertThrows(clz, () -> WordCount.main(params));
    }

    static Stream<Arguments> given_incorrect_output_when_WordCount_then_exception_parameters() {
        return Stream.of(
            Arguments.arguments("null output", new String[] { "--input", INPUT_PATH, "--output", null },
                RuntimeException.class),
            Arguments.arguments("empty output", new String[] { "--input", INPUT_PATH, "--output", "" },
                IllegalArgumentException.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("given_incorrect_output_when_WordCount_then_exception_parameters")
    public void given_incorrect_output_when_WordCount_then_exception(String name, String[] params,
        Class<Exception> clz) {
        Assert.assertThrows(clz, () -> WordCount.main(params));
    }

    private static String getResourceFile(String filePath) {
        return Objects.requireNonNull(WordCountTest.class.getResource(filePath)).getFile();
    }
}