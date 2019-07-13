package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RolloutPhaseListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("RolloutPhaseList", "RolloutPhase");

    private final List<RolloutPhaseTestData> elements = new ArrayList<>();

    public RolloutPhaseListTestData(RolloutPhaseTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static RolloutPhaseListTestData RolloutPhaseList(RolloutPhaseTestData... elements) {
        return new RolloutPhaseListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(RolloutPhaseTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}