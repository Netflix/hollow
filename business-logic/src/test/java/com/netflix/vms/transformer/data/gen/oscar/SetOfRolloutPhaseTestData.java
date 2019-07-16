package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

public class SetOfRolloutPhaseTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("SetOfRolloutPhase", "RolloutPhase");

    private static ToIntFunction<RolloutPhaseTestData> hashFunction = null;

    private final List<RolloutPhaseTestData> elements = new ArrayList<>();

    public SetOfRolloutPhaseTestData(RolloutPhaseTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SetOfRolloutPhaseTestData SetOfRolloutPhase(RolloutPhaseTestData... elements) {
        return new SetOfRolloutPhaseTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<RolloutPhaseTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(RolloutPhaseTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}