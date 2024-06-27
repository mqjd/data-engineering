package org.mqjd.flink.jobs.chapter3.section2.core;

import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeutils.base.array.BytePrimitiveArraySerializer;
import org.apache.flink.streaming.api.operators.SourceOperator;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

public class States {

    /**
     * @see SourceOperator#SPLITS_STATE_DESC
     */
    public static final ListStateDescriptor<byte[]> SPLITS_STATE_DESC =
        new ListStateDescriptor<>("SourceReaderState", BytePrimitiveArraySerializer.INSTANCE);

    public static final String SOURCE_SPLITS_STATE_DESC = SPLITS_STATE_DESC.getName();

    /**
     * @see CountTrigger#stateDesc
     */
    public static final String COUNT_TRIGGER_STATE_DESC = "count";
}
