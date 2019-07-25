package com.netflix.vms.transformer.data.gen.topn;

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

public class SetOfTopNAttributeTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("SetOfTopNAttribute", "TopNAttribute", "country");

    private static ToIntFunction<TopNAttributeTestData> hashFunction = null;

    private final List<TopNAttributeTestData> elements = new ArrayList<>();

    public SetOfTopNAttributeTestData(TopNAttributeTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SetOfTopNAttributeTestData SetOfTopNAttribute(TopNAttributeTestData... elements) {
        return new SetOfTopNAttributeTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<TopNAttributeTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(TopNAttributeTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}