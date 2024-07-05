package org.mqjd.flink.jobs.chapter1.section3.windowing;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.mqjd.flink.jobs.chapter1.section3.source.RunMetric;

public class TopSpeedAggregateFunction implements AggregateFunction<RunMetric, TopSpeedAccumulator, RunMetric> {
    private static final long serialVersionUID = -5776350726334361144L;

    @Override
    public TopSpeedAccumulator createAccumulator() {
        return new TopSpeedAccumulator();
    }

    @Override
    public TopSpeedAccumulator add(RunMetric value, TopSpeedAccumulator accumulator) {
        accumulator.add(value);
        return accumulator;
    }

    @Override
    public RunMetric getResult(TopSpeedAccumulator accumulator) {
        return accumulator.getResult();
    }

    @Override
    public TopSpeedAccumulator merge(TopSpeedAccumulator a, TopSpeedAccumulator b) {
        a.add(b.getResult());
        return a;
    }
}
