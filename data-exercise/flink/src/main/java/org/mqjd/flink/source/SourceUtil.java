package org.mqjd.flink.source;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.flink.util.function.SerializableFunction;

public class SourceUtil {
    public static <IN> FileSource<IN> createSimpleCsvSource(Class<IN> clz, Path... inputs) {
        SerializableFunction<CsvMapper, CsvSchema> schemaGenerator =
            mapper -> mapper.schemaFor(clz).withHeader().withColumnSeparator(',');
        CsvReaderFormat<IN> csvFormat =
            CsvReaderFormat.forSchema(CsvMapper::new, schemaGenerator, TypeInformation.of(clz));
        return FileSource.forRecordStreamFormat(csvFormat, inputs).build();
    }
}
