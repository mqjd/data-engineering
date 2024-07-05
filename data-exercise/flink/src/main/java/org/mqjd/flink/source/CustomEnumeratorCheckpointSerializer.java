package org.mqjd.flink.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;

public class CustomEnumeratorCheckpointSerializer extends CustomIteratorSourceSplitSerializer
    implements SimpleVersionedSerializer<Collection<CustomIteratorSourceSplit>> {

    private static final int CURRENT_VERSION = 1;

    @Override
    public int getVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public byte[] serialize(Collection<CustomIteratorSourceSplit> obj) throws IOException {
        final DataOutputSerializer out = new DataOutputSerializer(4 + getSize() * obj.size());
        out.writeInt(obj.size());
        for (CustomIteratorSourceSplit split : obj) {
            serializeV1(out, split);
        }
        return out.getCopyOfBuffer();
    }

    @Override
    public Collection<CustomIteratorSourceSplit> deserialize(int version, byte[] serialized) throws IOException {
        if (version != CURRENT_VERSION) {
            throw new IOException("Unrecognized version:" + version);
        }
        final DataInputDeserializer in = new DataInputDeserializer(serialized);
        int count = in.readInt();
        List<CustomIteratorSourceSplit> splits = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            splits.add(deserializeV1(in));
        }
        return splits;
    }
}
