package org.mqjd.flink.table.log;

import java.io.Serializable;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.data.RowData;

public class LogSinkFunction implements SinkFunction<RowData>, Serializable {

    private final DynamicTableSink.DataStructureConverter converter;

    public LogSinkFunction(DynamicTableSink.DataStructureConverter dataStructureConverter) {
        this.converter = dataStructureConverter;
    }

    @Override
    public void invoke(RowData value, Context context) throws Exception {
        System.out.println(converter.toExternal(value));
    }
}
