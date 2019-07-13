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

public class IPLDerivativeSetTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("IPLDerivativeSet", "IPLArtworkDerivative");

    private static ToIntFunction<IPLArtworkDerivativeTestData> hashFunction = null;

    private final List<IPLArtworkDerivativeTestData> elements = new ArrayList<>();

    public IPLDerivativeSetTestData(IPLArtworkDerivativeTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static IPLDerivativeSetTestData IPLDerivativeSet(IPLArtworkDerivativeTestData... elements) {
        return new IPLDerivativeSetTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<IPLArtworkDerivativeTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(IPLArtworkDerivativeTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}