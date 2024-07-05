package org.mqjd.flink.jobs.chapter1.section3.source;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.mqjd.flink.util.JsonUtil;

public class RunMetricGeneratorFunction implements GeneratorFunction<Long, RunMetric> {
    private static final long serialVersionUID = -2158355894836795654L;
    private final RunMetric[] lastMetrics;
    private int nextUser;

    public RunMetricGeneratorFunction(int numOfUsers) {
        lastMetrics =
            IntStream.range(1, numOfUsers + 1).mapToObj(RunMetricBuilder::buildDefault).toArray(RunMetric[]::new);
    }

    @Override
    public RunMetric map(Long numOfUsers) throws Exception {
        RunMetric lastMetric = lastMetrics[nextUser];
        RunMetricBuilder builder = RunMetricBuilder.builder().from(lastMetric);
        int newPace = Math.max(240, lastMetric.getPace() + RandomUtils.nextInt(0, 120) - 60);
        newPace = Math.min(480, newPace);
        builder.withPace(newPace);
        long newTimestamp = System.currentTimeMillis();

        Duration between = Duration.of(newTimestamp - lastMetric.getTimestamp(), ChronoUnit.MILLIS);
        long seconds = between.getSeconds();
        builder.withTimestamp(newTimestamp + seconds * 30000);
        BigDecimal deltaDistance = new BigDecimal(1000);
        double newDistance = deltaDistance.divide(BigDecimal.valueOf(newPace), RoundingMode.HALF_UP)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue() * seconds * 30 + lastMetric.getDistance();
        builder.withDistance(newDistance);
        nextUser = (++nextUser) % lastMetrics.length;
        RunMetric runMetric = builder.build();
        System.out.println(JsonUtil.toJson(runMetric));
        if (lastMetric.getDistance() > newDistance) {
            System.out.println(1);
        }
        return runMetric;
    }
}
