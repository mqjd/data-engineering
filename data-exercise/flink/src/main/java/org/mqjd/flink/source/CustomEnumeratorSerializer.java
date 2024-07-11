package org.mqjd.flink.source;

import static org.apache.flink.util.Preconditions.checkArgument;

import java.io.IOException;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.core.memory.DataInputDeserializer;
import org.apache.flink.core.memory.DataOutputSerializer;

public class CustomEnumeratorSerializer extends CustomIteratorSourceSplitSerializer
    implements SimpleVersionedSerializer<CustomIteratorSourceSplit> {

    private static final int CURRENT_VERSION = 1;

    @Override
    public int getVersion() {
        return CURRENT_VERSION;
    }

    @Override
    public byte[] serialize(CustomIteratorSourceSplit split) throws IOException {
        checkArgument(split.getClass() == CustomIteratorSourceSplit.class, "cannot serialize subclasses");
        final DataOutputSerializer out = new DataOutputSerializer(getSize());
        serializeV1(out, split);
        return out.getCopyOfBuffer();
    }

    @Override
    public CustomIteratorSourceSplit deserialize(int version, byte[] serialized) throws IOException {
        if (version != CURRENT_VERSION) {
            throw new IOException("Unrecognized version:" + version);
        }
        final DataInputDeserializer in = new DataInputDeserializer(serialized);
        return deserializeV1(in);
    }
}
