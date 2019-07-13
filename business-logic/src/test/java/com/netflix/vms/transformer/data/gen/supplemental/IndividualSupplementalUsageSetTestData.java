package com.netflix.vms.transformer.data.gen.supplemental;

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

public class IndividualSupplementalUsageSetTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("IndividualSupplementalUsageSet", "String", "value");

    private static ToIntFunction<StringTestData> hashFunction = null;

    private final List<StringTestData> elements = new ArrayList<>();

    public IndividualSupplementalUsageSetTestData(StringTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static IndividualSupplementalUsageSetTestData IndividualSupplementalUsageSet(StringTestData... elements) {
        return new IndividualSupplementalUsageSetTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<StringTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(StringTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}