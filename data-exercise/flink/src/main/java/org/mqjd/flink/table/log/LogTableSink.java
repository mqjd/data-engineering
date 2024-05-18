package org.mqjd.flink.table.log;

import java.io.Serializable;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.types.DataType;

public class LogTableSink implements DynamicTableSink, Serializable {
    private final DataType dataType;

    public LogTableSink(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        return requestedMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        DataStructureConverter dataStructureConverter = context.createDataStructureConverter(dataType);
        return SinkFunctionProvider.of(new LogSinkFunction(dataStructureConverter));
    }

    @Override
    public DynamicTableSink copy() {
        return null;
    }

    @Override
    public String asSummaryString() {
        return null;
    }
}
