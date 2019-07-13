package com.netflix.vms.transformer.data.gen.mceImage;

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

public class IPLDerivativeGroupSetTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("IPLDerivativeGroupSet", "IPLDerivativeGroup", "externalId", "imageType", "submission");

    private static ToIntFunction<IPLDerivativeGroupTestData> hashFunction = null;

    private final List<IPLDerivativeGroupTestData> elements = new ArrayList<>();

    public IPLDerivativeGroupSetTestData(IPLDerivativeGroupTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static IPLDerivativeGroupSetTestData IPLDerivativeGroupSet(IPLDerivativeGroupTestData... elements) {
        return new IPLDerivativeGroupSetTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<IPLDerivativeGroupTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(IPLDerivativeGroupTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}