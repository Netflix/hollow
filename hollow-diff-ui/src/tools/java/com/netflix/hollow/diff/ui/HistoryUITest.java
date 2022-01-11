package com.netflix.hollow.diff.ui;

import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryUITest {

    @Test
    public void startServerOnPort7777() throws Exception {
        HollowHistory history = createHistory();
        HollowHistory historyReverse = createHistoryReverse();

        HollowHistoryUIServer server = new HollowHistoryUIServer(history, 7777);
        HollowHistoryUIServer serverReverse = new HollowHistoryUIServer(historyReverse, 7778);
        server.start();
        serverReverse.start();
        server.join();
        serverReverse.join();
    }


    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;
    private HollowObjectSchema bSchema;

    private HollowHistory createHistory() throws IOException {
        //setup producer
        stateEngine = new HollowWriteStateEngine();

        //initalalize schema
        schema = new HollowObjectSchema("Movie", 1);
        //add columns/fields to initialized schema
        schema.addField("id", FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        //attach schema to write state engine
        stateEngine.addTypeState(writeState);

        //1) add records
        addRecord(0);

        //add rec to write phase
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        //write snapshot to output stream
        writer.writeSnapshot(baos);

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        //load snapshot from output stream to read state engine
        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));
        HollowHistory history = new HollowHistory(readStateEngine, 0, 10);
        //setup key index
        history.getKeyIndex().addTypeIndex("Movie", "id");

        applyDeltaState(history, Arrays.asList(0, 1), 1L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1, 2), 2L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1, 2, 3), 3L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1, 2), 4L, writer, reader);
        applyDeltaState(history, Arrays.asList(0, 1), 5L, writer, reader);


        return history;
    }

    private HollowHistory createHistoryReverse() throws IOException {
        //setup producer
        stateEngine = new HollowWriteStateEngine();

        //initalalize schema
        schema = new HollowObjectSchema("Movie", 1);
        //add columns/fields to initialized schema
        schema.addField("id", FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        //attach schema to write state engine
        stateEngine.addTypeState(writeState);

        //1) add records    - corresponding to v5 of createHistory()
        addRecord(0);

        //add rec to write phase
        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        Map<Long, ByteArrayOutputStream> reverseDeltaBaos = new HashMap<>();    // a map version v -> Baos containing reverse delta to get to version v
        createReverseDeltaStateFor(Arrays.asList(0, 1), 0L, 1L, reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1, 2), 1L, 2L, reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1, 2, 3), 2L, 3L, reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1, 2), 3L, 4L,  reverseDeltaBaos, writer);
        createReverseDeltaStateFor(Arrays.asList(0, 1), 4L, 5L, reverseDeltaBaos, writer);

        ByteArrayOutputStream baos_v5 = new ByteArrayOutputStream();
        //write snapshot to output stream
        writer.writeSnapshot(baos_v5);

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        //load snapshot from output stream to read state engine
        reader.readSnapshot(HollowBlobInput.serial(baos_v5.toByteArray()));
        //>>>do not init history with the snapshot
        HollowHistory history = new HollowHistory(readStateEngine, 5L, 10);
        history.getKeyIndex().addTypeIndex("Movie", "id");

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(4L).toByteArray()));
        history.reverseDeltaOccurred(5L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(3L).toByteArray()));
        history.reverseDeltaOccurred(4L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(2L).toByteArray()));
        history.reverseDeltaOccurred(3L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(1L).toByteArray()));
        history.reverseDeltaOccurred(2L);

        reader.applyDelta(HollowBlobInput.serial(reverseDeltaBaos.get(0L).toByteArray()));
        history.reverseDeltaOccurred(1L);

        return history;
    }

    private void createReverseDeltaStateFor(List<Integer> values, long fromVersion, long toVersion, Map<Long, ByteArrayOutputStream> reverseDeltaBaosMap, HollowBlobWriter writer) throws IOException {
        //write phase to add rec phase
        stateEngine.prepareForNextCycle();

        //2) add new set of records
        for (int v : values) {
            addRecord(v);
        }

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baosDelta = new ByteArrayOutputStream();
        ByteArrayOutputStream baosReverseDelta = new ByteArrayOutputStream();
        //write delta based on new records
        writer.writeDelta(baosDelta);
        writer.writeReverseDelta(baosReverseDelta);
        reverseDeltaBaosMap.put(fromVersion, baosReverseDelta);
    }

    private void applyDeltaState(HollowHistory history, List<Integer> values, long toVersion, HollowBlobWriter writer, HollowBlobReader reader) throws IOException {
        //write phase to add rec phase
        stateEngine.prepareForNextCycle();

        //2) add new set of records
        for (int v : values) {
            addRecord(v);
        }

        //add rec to write phase
        stateEngine.prepareForWrite();

        //reinit output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //write delta based on new records
        writer.writeDelta(baos);

        //apply delta to snapshot generate in 1
        reader.applyDelta(HollowBlobInput.serial(baos.toByteArray()));

        history.deltaOccurred(toVersion);
    }

    private void addRecord(int id) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("id", id);
        stateEngine.add("Movie", rec);
    }
}
