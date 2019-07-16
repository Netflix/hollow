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

public class SetOfPhaseRequiredImageTypeTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("SetOfPhaseRequiredImageType", "PhaseRequiredImageType");

    private static ToIntFunction<PhaseRequiredImageTypeTestData> hashFunction = null;

    private final List<PhaseRequiredImageTypeTestData> elements = new ArrayList<>();

    public SetOfPhaseRequiredImageTypeTestData(PhaseRequiredImageTypeTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SetOfPhaseRequiredImageTypeTestData SetOfPhaseRequiredImageType(PhaseRequiredImageTypeTestData... elements) {
        return new SetOfPhaseRequiredImageTypeTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<PhaseRequiredImageTypeTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(PhaseRequiredImageTypeTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}