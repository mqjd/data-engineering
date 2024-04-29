package org.mqjd.flink.function.agg;

import org.apache.flink.streaming.api.functions.aggregation.AggregationFunction;
import org.apache.flink.streaming.api.functions.aggregation.SumFunction;

public class SumAggregator<T extends Number> extends AggregationFunction<T> {

    private final SumFunction adder;

    public SumAggregator(Class<T> type) {
        adder = SumFunction.getForClass(type);
    }

    @Override
    public T reduce(T value1, T value2) {
        //noinspection unchecked
        return (T) adder.add(value1, value2);
    }
}
