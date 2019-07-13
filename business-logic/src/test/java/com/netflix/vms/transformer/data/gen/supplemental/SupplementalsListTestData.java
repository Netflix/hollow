package com.netflix.vms.transformer.data.gen.supplemental;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupplementalsListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("SupplementalsList", "IndividualSupplemental");

    private final List<IndividualSupplementalTestData> elements = new ArrayList<>();

    public SupplementalsListTestData(IndividualSupplementalTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SupplementalsListTestData SupplementalsList(IndividualSupplementalTestData... elements) {
        return new SupplementalsListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(IndividualSupplementalTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}