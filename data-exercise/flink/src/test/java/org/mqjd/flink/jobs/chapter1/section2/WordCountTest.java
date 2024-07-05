package org.mqjd.flink.jobs.chapter1.section2;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mqjd.flink.jobs.FlinkJobTest;

public class WordCountTest extends FlinkJobTest {

    private static final String WORK_PATH = "/jobs/chapter1/section2";
    public static final String INPUT_PATH = getResourceFile(String.format("%s/word-count.input.txt", WORK_PATH));
    private static final String EXPECTED_PATH = String.format("%s/word-count.expected.txt", WORK_PATH);
    private static final String OUTPUT_PATH = String.format("target%s/output", WORK_PATH);

    @Test
    public void given_correct_input_and_output_when_WordCount_then_success() throws Exception {
        deleteDirectory(OUTPUT_PATH);
        String[] params = { "--input", INPUT_PATH, "--output", OUTPUT_PATH };
        WordCount.main(params);
        compareResultsByLines(EXPECTED_PATH, OUTPUT_PATH);
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

}