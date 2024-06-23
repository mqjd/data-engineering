package org.mqjd.flink.source;

import java.io.IOException;
import java.util.Collection;

import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputView;

public class CustomIteratorSourceSplitSerializer {

    public static CustomEnumeratorSerializer singleSerializer() {
        return new CustomEnumeratorSerializer();
    }

    public static SimpleVersionedSerializer<Collection<CustomIteratorSourceSplit>> collectionSerializer() {
        return new CustomEnumeratorCheckpointSerializer();
    }

    public int getSize() {
        return 24;
    }

    public void serializeV1(DataOutputView out, CustomIteratorSourceSplit split) throws IOException {
        out.writeLong(split.getMessageCount());
        out.writeLong(split.getCurrent());
        out.writeInt(split.getNumSplits());
        out.writeInt(split.getSplitId());
    }

    public CustomIteratorSourceSplit deserializeV1(DataInputDeserializer in) throws IOException {
        return new CustomIteratorSourceSplit(in.readLong(), in.readLong(), in.readInt(), in.readInt());
    }

}
