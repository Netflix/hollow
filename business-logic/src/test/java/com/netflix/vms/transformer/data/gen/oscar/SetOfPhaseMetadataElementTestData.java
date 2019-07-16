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

public class SetOfPhaseMetadataElementTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("SetOfPhaseMetadataElement", "PhaseMetadataElement");

    private static ToIntFunction<PhaseMetadataElementTestData> hashFunction = null;

    private final List<PhaseMetadataElementTestData> elements = new ArrayList<>();

    public SetOfPhaseMetadataElementTestData(PhaseMetadataElementTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SetOfPhaseMetadataElementTestData SetOfPhaseMetadataElement(PhaseMetadataElementTestData... elements) {
        return new SetOfPhaseMetadataElementTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<PhaseMetadataElementTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(PhaseMetadataElementTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}