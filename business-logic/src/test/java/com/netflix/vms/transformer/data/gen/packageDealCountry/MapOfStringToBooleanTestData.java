package com.netflix.vms.transformer.data.gen.packageDealCountry;

import static com.netflix.hollow.api.testdata.HollowTestDataMapEntry.entry;

import com.netflix.hollow.api.testdata.HollowTestDataMapEntry;
import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

public class MapOfStringToBooleanTestData extends HollowTestRecord {

    private static final HollowMapSchema SCHEMA = new HollowMapSchema("MapOfStringToBoolean", "String", "Boolean", "value");

    private static ToIntFunction<StringTestData> hashFunction = null;

    private final List<HollowTestDataMapEntry<StringTestData, BooleanTestData>> elements = new ArrayList<>();

    @SafeVarargs
    public MapOfStringToBooleanTestData(HollowTestDataMapEntry<StringTestData, BooleanTestData>... entries) {
        this.elements.addAll(Arrays.asList(entries));
    }

    @SafeVarargs
    public static MapOfStringToBooleanTestData MapOfStringToBoolean(HollowTestDataMapEntry<StringTestData, BooleanTestData>... elements) {
        return new MapOfStringToBooleanTestData(elements);
    }

    public static MapOfStringToBooleanTestData MapOfStringToBoolean(StringTestData key, BooleanTestData value) {
        return MapOfStringToBoolean(entry(key, value));
    }

    public static MapOfStringToBooleanTestData MapOfStringToBoolean(
            StringTestData key1, BooleanTestData value1,
            StringTestData key2, BooleanTestData value2) {
        return MapOfStringToBoolean(entry(key1, value1), entry(key2, value2));
    }

    public static MapOfStringToBooleanTestData MapOfStringToBoolean(
            StringTestData key1, BooleanTestData value1,
            StringTestData key2, BooleanTestData value2,
            StringTestData key3, BooleanTestData value3) {
        return MapOfStringToBoolean(entry(key1, value1), entry(key2, value2), entry(key3, value3));
    }

    public static MapOfStringToBooleanTestData MapOfStringToBoolean(
            StringTestData key1, BooleanTestData value1,
            StringTestData key2, BooleanTestData value2,
            StringTestData key3, BooleanTestData value3,
            StringTestData key4, BooleanTestData value4) {
        return MapOfStringToBoolean(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4));
    }

    public static MapOfStringToBooleanTestData MapOfStringToBoolean(
            StringTestData key1, BooleanTestData value1,
            StringTestData key2, BooleanTestData value2,
            StringTestData key3, BooleanTestData value3,
            StringTestData key4, BooleanTestData value4,
            StringTestData key5, BooleanTestData value5) {
        return MapOfStringToBoolean(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4), entry(key5, value5));
    }

    public static void setHashFunction(ToIntFunction<StringTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(HollowTestDataMapEntry<StringTestData, BooleanTestData> e : elements) {
            if(hashFunction == null)
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine));
            else
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine), hashFunction.applyAsInt(e.key()));
        }
        return rec;
    }

}