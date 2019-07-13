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

public class RolloutPhaseArtworkSourceFileIdListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("RolloutPhaseArtworkSourceFileIdList", "RolloutPhaseArtworkSourceFileId");

    private final List<RolloutPhaseArtworkSourceFileIdTestData> elements = new ArrayList<>();

    public RolloutPhaseArtworkSourceFileIdListTestData(RolloutPhaseArtworkSourceFileIdTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static RolloutPhaseArtworkSourceFileIdListTestData RolloutPhaseArtworkSourceFileIdList(RolloutPhaseArtworkSourceFileIdTestData... elements) {
        return new RolloutPhaseArtworkSourceFileIdListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(RolloutPhaseArtworkSourceFileIdTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}