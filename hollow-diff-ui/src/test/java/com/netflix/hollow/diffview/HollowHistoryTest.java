package com.netflix.hollow.diffview;

import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.history.ui.jetty.HollowHistoryUIServer;
import com.netflix.hollow.tools.history.HollowHistory;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowHistoryTest extends AbstractStateEngineTest {

    private HollowObjectSchema aSchema;
    private HollowObjectSchema bSchema;

    private static final String B_TYPE = "B";
    private static final String B_FN_PREFIX = "b";
    
    @Override
    @Before
    public void setUp() {
        aSchema = new HollowObjectSchema("A", 3, "a1");
        aSchema.addField("a1", HollowObjectSchema.FieldType.INT);
        aSchema.addField("a2", HollowObjectSchema.FieldType.INT);
        aSchema.addField("a3", HollowObjectSchema.FieldType.INT);

        bSchema = new HollowObjectSchema(B_TYPE, 2,  B_FN_PREFIX + "1");
        bSchema.addField(B_FN_PREFIX + "1", HollowObjectSchema.FieldType.INT);
        bSchema.addField(B_FN_PREFIX + "2", HollowObjectSchema.FieldType.INT);

        super.setUp();
    }

    @Test
    public void testNewTypeInHistory() throws Exception {
        addRecord(1, 2, 3);
        roundTripSnapshot();

        HollowHistory history = new HollowHistory(readStateEngine, 1L, 10);
        setupKeyIndex(readStateEngine, history);

        {
            initWriteStateEngine();
            addRecord(1, 12, 13);
            roundTripSnapshot();
            history.doubleSnapshotOccurred(readStateEngine,2L);
        }

        // Double Snapshot - With New Type
        {
            initWriteStateEngine();
            addRecord(bSchema, B_FN_PREFIX, 1, 2);

            roundTripSnapshot();
            setupKeyIndex(readStateEngine, history);
            history.doubleSnapshotOccurred(readStateEngine, 3L);
        }

        HollowHistoryUIServer server = new HollowHistoryUIServer(history, 7777);
        Assert.assertNotNull(server.getUI().getHistory().getLatestState().getNonNullSchema(B_TYPE));
    }

    private void setupKeyIndex(HollowReadStateEngine stateEngine, HollowHistory history) {
        HollowHistoryKeyIndex keyIndex = history.getKeyIndex();
        for (String type : stateEngine.getAllTypes()) {

            HollowTypeReadState typeState = stateEngine.getTypeState(type);
            HollowSchema schema = typeState.getSchema();
            if (schema instanceof HollowObjectSchema) {
                HollowObjectSchema oSchema = (HollowObjectSchema) schema;
                PrimaryKey pKey = oSchema.getPrimaryKey();
                if (pKey == null) {
                    continue;
                }

                keyIndex.indexTypeField(pKey, stateEngine);
            }
        }
    }

    private void addRecord(int a1, int a2, int a3) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(aSchema);

        rec.setInt("a1", a1);
        rec.setInt("a2", a2);
        rec.setInt("a3", a3);

        writeStateEngine.add("A", rec);
    }

    private void addRecord(HollowObjectSchema schema, String fnPrefix, int ... vals) {
        String bType = schema.getName();
        if (writeStateEngine.getTypeState(bType) == null) {
            writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
        }

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < vals.length; i++) {
            String fn = fnPrefix + (i + 1);
            rec.setInt(fn, vals[i]);
        }

        writeStateEngine.add(bType, rec);
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(aSchema));
    }

}