package com.netflix.vms.transformer.data.gen.gatekeeper2;

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

public class MapOfFlagsFirstDisplayDatesTestData extends HollowTestRecord {

    private static final HollowMapSchema SCHEMA = new HollowMapSchema("MapOfFlagsFirstDisplayDates", "MapKey", "Date", "value");

    private static ToIntFunction<MapKeyTestData> hashFunction = null;

    private final List<HollowTestDataMapEntry<MapKeyTestData, DateTestData>> elements = new ArrayList<>();

    @SafeVarargs
    public MapOfFlagsFirstDisplayDatesTestData(HollowTestDataMapEntry<MapKeyTestData, DateTestData>... entries) {
        this.elements.addAll(Arrays.asList(entries));
    }

    @SafeVarargs
    public static MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(HollowTestDataMapEntry<MapKeyTestData, DateTestData>... elements) {
        return new MapOfFlagsFirstDisplayDatesTestData(elements);
    }

    public static MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(MapKeyTestData key, DateTestData value) {
        return MapOfFlagsFirstDisplayDates(entry(key, value));
    }

    public static MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(
            MapKeyTestData key1, DateTestData value1,
            MapKeyTestData key2, DateTestData value2) {
        return MapOfFlagsFirstDisplayDates(entry(key1, value1), entry(key2, value2));
    }

    public static MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(
            MapKeyTestData key1, DateTestData value1,
            MapKeyTestData key2, DateTestData value2,
            MapKeyTestData key3, DateTestData value3) {
        return MapOfFlagsFirstDisplayDates(entry(key1, value1), entry(key2, value2), entry(key3, value3));
    }

    public static MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(
            MapKeyTestData key1, DateTestData value1,
            MapKeyTestData key2, DateTestData value2,
            MapKeyTestData key3, DateTestData value3,
            MapKeyTestData key4, DateTestData value4) {
        return MapOfFlagsFirstDisplayDates(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4));
    }

    public static MapOfFlagsFirstDisplayDatesTestData MapOfFlagsFirstDisplayDates(
            MapKeyTestData key1, DateTestData value1,
            MapKeyTestData key2, DateTestData value2,
            MapKeyTestData key3, DateTestData value3,
            MapKeyTestData key4, DateTestData value4,
            MapKeyTestData key5, DateTestData value5) {
        return MapOfFlagsFirstDisplayDates(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4), entry(key5, value5));
    }

    public static void setHashFunction(ToIntFunction<MapKeyTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(HollowTestDataMapEntry<MapKeyTestData, DateTestData> e : elements) {
            if(hashFunction == null)
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine));
            else
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine), hashFunction.applyAsInt(e.key()));
        }
        return rec;
    }

}