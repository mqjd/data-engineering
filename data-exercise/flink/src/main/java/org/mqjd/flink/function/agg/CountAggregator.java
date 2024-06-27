package org.mqjd.flink.function.agg;


import org.apache.flink.api.common.functions.AggregateFunction;

public class CountAggregator<IN> implements AggregateFunction<IN, Long, Long> {

    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(IN value, Long accumulator) {
        return accumulator + 1;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}
