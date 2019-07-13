package com.netflix.vms.transformer.data.gen.mceImage;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfDerivativeTagTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ListOfDerivativeTag", "DerivativeTag");

    private final List<DerivativeTagTestData> elements = new ArrayList<>();

    public ListOfDerivativeTagTestData(DerivativeTagTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ListOfDerivativeTagTestData ListOfDerivativeTag(DerivativeTagTestData... elements) {
        return new ListOfDerivativeTagTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(DerivativeTagTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}