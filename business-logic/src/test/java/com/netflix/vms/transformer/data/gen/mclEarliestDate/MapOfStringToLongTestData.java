package com.netflix.vms.transformer.data.gen.mclEarliestDate;

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

public class MapOfStringToLongTestData extends HollowTestRecord {

    private static final HollowMapSchema SCHEMA = new HollowMapSchema("MapOfStringToLong", "String", "Long", "value");

    private static ToIntFunction<StringTestData> hashFunction = null;

    private final List<HollowTestDataMapEntry<StringTestData, LongTestData>> elements = new ArrayList<>();

    @SafeVarargs
    public MapOfStringToLongTestData(HollowTestDataMapEntry<StringTestData, LongTestData>... entries) {
        this.elements.addAll(Arrays.asList(entries));
    }

    @SafeVarargs
    public static MapOfStringToLongTestData MapOfStringToLong(HollowTestDataMapEntry<StringTestData, LongTestData>... elements) {
        return new MapOfStringToLongTestData(elements);
    }

    public static MapOfStringToLongTestData MapOfStringToLong(StringTestData key, LongTestData value) {
        return MapOfStringToLong(entry(key, value));
    }

    public static MapOfStringToLongTestData MapOfStringToLong(
            StringTestData key1, LongTestData value1,
            StringTestData key2, LongTestData value2) {
        return MapOfStringToLong(entry(key1, value1), entry(key2, value2));
    }

    public static MapOfStringToLongTestData MapOfStringToLong(
            StringTestData key1, LongTestData value1,
            StringTestData key2, LongTestData value2,
            StringTestData key3, LongTestData value3) {
        return MapOfStringToLong(entry(key1, value1), entry(key2, value2), entry(key3, value3));
    }

    public static MapOfStringToLongTestData MapOfStringToLong(
            StringTestData key1, LongTestData value1,
            StringTestData key2, LongTestData value2,
            StringTestData key3, LongTestData value3,
            StringTestData key4, LongTestData value4) {
        return MapOfStringToLong(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4));
    }

    public static MapOfStringToLongTestData MapOfStringToLong(
            StringTestData key1, LongTestData value1,
            StringTestData key2, LongTestData value2,
            StringTestData key3, LongTestData value3,
            StringTestData key4, LongTestData value4,
            StringTestData key5, LongTestData value5) {
        return MapOfStringToLong(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4), entry(key5, value5));
    }

    public static void setHashFunction(ToIntFunction<StringTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(HollowTestDataMapEntry<StringTestData, LongTestData> e : elements) {
            if(hashFunction == null)
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine));
            else
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine), hashFunction.applyAsInt(e.key()));
        }
        return rec;
    }

}