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

public class SetOfMovieExtensionOverrideTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("SetOfMovieExtensionOverride", "MovieExtensionOverride");

    private static ToIntFunction<MovieExtensionOverrideTestData> hashFunction = null;

    private final List<MovieExtensionOverrideTestData> elements = new ArrayList<>();

    public SetOfMovieExtensionOverrideTestData(MovieExtensionOverrideTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SetOfMovieExtensionOverrideTestData SetOfMovieExtensionOverride(MovieExtensionOverrideTestData... elements) {
        return new SetOfMovieExtensionOverrideTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<MovieExtensionOverrideTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(MovieExtensionOverrideTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}