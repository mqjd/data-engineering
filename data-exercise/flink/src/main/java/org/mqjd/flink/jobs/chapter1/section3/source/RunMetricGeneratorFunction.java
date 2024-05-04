package org.mqjd.flink.jobs.chapter1.section3.source;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Random;

import org.apache.flink.connector.datagen.source.GeneratorFunction;

public class RunMetricGeneratorFunction implements GeneratorFunction<Long, RunMetric> {
    @Serial
    private static final long serialVersionUID = -2158355894836795654L;
    private final RunMetric[] lastMetrics;
    private int nextUser;
    private final Random rand = new Random();

    public RunMetricGeneratorFunction(int numOfUsers) {
        lastMetrics = new RunMetric[numOfUsers];
        Arrays.fill(lastMetrics, RunMetricBuilder.buildDefault());
    }

    @Override
    public RunMetric map(Long numOfUsers) throws Exception {
        RunMetric lastMetric = lastMetrics[nextUser];
        RunMetricBuilder builder = RunMetricBuilder.builder().from(lastMetric);
        int newPace = Math.min(360, lastMetric.getPace() + rand.nextInt(-60, 60));
        builder.withPace(newPace);
        Long newTimestamp = System.currentTimeMillis();
        builder.withTimestamp(newTimestamp);

        Duration between = Duration.of(newTimestamp - lastMetric.getTimestamp(), ChronoUnit.MILLIS);
        long seconds = between.getSeconds();
        BigDecimal deltaDistance = new BigDecimal(1000);
        double newDistance = deltaDistance.divide(BigDecimal.valueOf(newPace), RoundingMode.HALF_UP)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue() * seconds * 30 + lastMetric.getDistance();
        builder.withDistance(newDistance);
        nextUser = (++nextUser) % lastMetrics.length;
        return builder.build();
    }
}
