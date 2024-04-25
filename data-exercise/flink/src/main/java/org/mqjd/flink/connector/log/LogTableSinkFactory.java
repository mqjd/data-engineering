package org.mqjd.flink.connector.log;

import java.util.HashSet;
import java.util.Set;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.factories.DynamicTableSinkFactory;

public class LogTableSinkFactory implements DynamicTableSinkFactory {
    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {
        return new LogTableSink(context.getCatalogTable().getResolvedSchema().toPhysicalRowDataType());
    }

    @Override
    public String factoryIdentifier() {
        return "log";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return new HashSet<>();
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return new HashSet<>();
    }
}
