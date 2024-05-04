package org.mqjd.flink.jobs.chapter1.section3.windowing;

import java.io.Serial;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.mqjd.flink.jobs.chapter1.section3.source.RunMetric;

public class RangeTrigger extends Trigger<RunMetric, GlobalWindow> {
    @Serial
    private static final long serialVersionUID = 7402512908425711584L;

    private final long size;

    private final ValueStateDescriptor<Long> stateDesc =
        new ValueStateDescriptor<>("distance", LongSerializer.INSTANCE);

    public RangeTrigger(long size) {
        this.size = size;
    }

    @Override
    public TriggerResult onElement(RunMetric element, long timestamp, GlobalWindow window, TriggerContext ctx)
        throws Exception {
        ValueState<Long> curDistance = ctx.getPartitionedState(this.stateDesc);
        Long pre = curDistance.value();
        long distance = element.getDistance().longValue();
        long remainder = distance % size;
        long start = distance - remainder;
        curDistance.update(start);
        if (pre != null && start > pre) {
            return TriggerResult.FIRE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, GlobalWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }

    @Override
    public void clear(GlobalWindow window, TriggerContext ctx) throws Exception {
    }

    public static Trigger<? super RunMetric, ? super GlobalWindow> create(long size) {
        return new RangeTrigger(size);
    }
}
