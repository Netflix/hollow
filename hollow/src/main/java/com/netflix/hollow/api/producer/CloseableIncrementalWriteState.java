package com.netflix.hollow.api.producer;

import static com.netflix.hollow.api.producer.HollowIncrementalCyclePopulator.DELETE_RECORD;

import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.core.write.objectmapper.RecordPrimaryKey;
import com.netflix.hollow.core.write.objectmapper.flatrecords.FlatRecord;
import java.util.concurrent.ConcurrentHashMap;

// @@@ Move into HollowIncrementalCyclePopulator since the event map is a shared resource
//     HollowIncrementalCyclePopulator constructed from the write state instance
final class CloseableIncrementalWriteState implements HollowProducer.IncrementalWriteState, AutoCloseable {
    private final ConcurrentHashMap<RecordPrimaryKey, Object> events;
    private final HollowObjectMapper objectMapper;
    private volatile boolean closed;

    public CloseableIncrementalWriteState(
            ConcurrentHashMap<RecordPrimaryKey, Object> events,
            HollowObjectMapper objectMapper) {
        this.events = events;
        this.objectMapper = objectMapper;
    }

    @Override public void addOrModify(Object o) {
        ensureNotClosed();

        RecordPrimaryKey key;
        if (o instanceof FlatRecord) {
            FlatRecord fr = (FlatRecord) o;
            key = fr.getRecordPrimaryKey();
        } else {
            key = objectMapper.extractPrimaryKey(o);
        }

        events.put(key, o);
    }

    @Override public void delete(Object o) {
        ensureNotClosed();

        RecordPrimaryKey key;
        if (o instanceof FlatRecord) {
            FlatRecord fr = (FlatRecord) o;
            key = fr.getRecordPrimaryKey();
        } else {
            key = objectMapper.extractPrimaryKey(o);
        }

        delete(key);
    }

    @Override public void delete(RecordPrimaryKey key) {
        ensureNotClosed();

        // @@@ Deletion is silently ignored if no object exists for the key
        events.put(key, DELETE_RECORD);
    }

    private void ensureNotClosed() {
        if (closed) {
            throw new IllegalStateException(
                    "Write state operated on after the incremental population stage of a cycle");
        }
    }

    @Override public void close() {
        closed = true;
    }
}
