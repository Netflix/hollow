package com.netflix.hollow.diff.ui;

import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.HollowObjectSchema.FieldType;
import com.netflix.hollow.history.HollowHistory;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowObjectTypeWriteState;
import com.netflix.hollow.write.HollowObjectWriteRecord;
import com.netflix.hollow.write.HollowTypeWriteState;
import com.netflix.hollow.write.HollowWriteStateEngine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

public class HistoryUITest {

    @Test
    public void startServerOnPort7777() throws Exception {
        HollowHistory history = createHistory();

        HollowHistoryUIServer server = new HollowHistoryUIServer(history, 7777);
        server.start();
        server.join();
    }


    private HollowWriteStateEngine stateEngine;
    private HollowObjectSchema schema;

    private HollowHistory createHistory() throws IOException {
        stateEngine = new HollowWriteStateEngine();

        schema = new HollowObjectSchema("TypeA", 2);
        schema.addField("a1", FieldType.INT);
        schema.addField("a2", FieldType.INT);

        HollowTypeWriteState writeState = new HollowObjectTypeWriteState(schema);

        stateEngine.addTypeState(writeState);

        addRecord(1, 1);
        addRecord(2, 2);
        addRecord(3, 3);
        addRecord(4, 4);
        addRecord(5, 5);
        addRecord(6, 6);

        stateEngine.prepareForWrite();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        writer.writeSnapshot(baos);


        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readStateEngine);
        reader.readSnapshot(new ByteArrayInputStream(baos.toByteArray()));
        HollowHistory history = new HollowHistory(readStateEngine, 0, 10);
        history.getKeyIndex().addTypeIndex("TypeA", "a1");

        stateEngine.prepareForNextCycle();

        addRecord(1, 1);
        addRecord(2, 7);
        addRecord(3, 3);
        addRecord(5, 8);
        addRecord(6, 6);
        addRecord(7, 9);
        addRecord(8, 10);

        stateEngine.prepareForWrite();

        baos = new ByteArrayOutputStream();
        writer.writeDelta(baos);
        reader.applyDelta(new ByteArrayInputStream(baos.toByteArray()));
        history.deltaOccurred(19991231235959999L);

        stateEngine.prepareForNextCycle();

        addRecord(1, 1);
        addRecord(2, 7);
        addRecord(3, 11);
        addRecord(6, 12);
        addRecord(7, 13);
        addRecord(8, 10);

        stateEngine.prepareForWrite();

        baos = new ByteArrayOutputStream();
        writer.writeDelta(baos);
        reader.applyDelta(new ByteArrayInputStream(baos.toByteArray()));
        history.deltaOccurred(20001231235959999L);

        return history;
    }

    private void addRecord(int a1, int a2) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("a1", a1);
        rec.setInt("a2", a2);
        stateEngine.add("TypeA", rec);
    }


}
