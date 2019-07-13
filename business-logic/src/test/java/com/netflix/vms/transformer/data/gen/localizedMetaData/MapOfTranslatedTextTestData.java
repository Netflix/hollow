package com.netflix.vms.transformer.data.gen.localizedMetaData;

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

public class MapOfTranslatedTextTestData extends HollowTestRecord {

    private static final HollowMapSchema SCHEMA = new HollowMapSchema("MapOfTranslatedText", "MapKey", "TranslatedTextValue", "value");

    private static ToIntFunction<MapKeyTestData> hashFunction = null;

    private final List<HollowTestDataMapEntry<MapKeyTestData, TranslatedTextValueTestData>> elements = new ArrayList<>();

    @SafeVarargs
    public MapOfTranslatedTextTestData(HollowTestDataMapEntry<MapKeyTestData, TranslatedTextValueTestData>... entries) {
        this.elements.addAll(Arrays.asList(entries));
    }

    @SafeVarargs
    public static MapOfTranslatedTextTestData MapOfTranslatedText(HollowTestDataMapEntry<MapKeyTestData, TranslatedTextValueTestData>... elements) {
        return new MapOfTranslatedTextTestData(elements);
    }

    public static MapOfTranslatedTextTestData MapOfTranslatedText(MapKeyTestData key, TranslatedTextValueTestData value) {
        return MapOfTranslatedText(entry(key, value));
    }

    public static MapOfTranslatedTextTestData MapOfTranslatedText(
            MapKeyTestData key1, TranslatedTextValueTestData value1,
            MapKeyTestData key2, TranslatedTextValueTestData value2) {
        return MapOfTranslatedText(entry(key1, value1), entry(key2, value2));
    }

    public static MapOfTranslatedTextTestData MapOfTranslatedText(
            MapKeyTestData key1, TranslatedTextValueTestData value1,
            MapKeyTestData key2, TranslatedTextValueTestData value2,
            MapKeyTestData key3, TranslatedTextValueTestData value3) {
        return MapOfTranslatedText(entry(key1, value1), entry(key2, value2), entry(key3, value3));
    }

    public static MapOfTranslatedTextTestData MapOfTranslatedText(
            MapKeyTestData key1, TranslatedTextValueTestData value1,
            MapKeyTestData key2, TranslatedTextValueTestData value2,
            MapKeyTestData key3, TranslatedTextValueTestData value3,
            MapKeyTestData key4, TranslatedTextValueTestData value4) {
        return MapOfTranslatedText(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4));
    }

    public static MapOfTranslatedTextTestData MapOfTranslatedText(
            MapKeyTestData key1, TranslatedTextValueTestData value1,
            MapKeyTestData key2, TranslatedTextValueTestData value2,
            MapKeyTestData key3, TranslatedTextValueTestData value3,
            MapKeyTestData key4, TranslatedTextValueTestData value4,
            MapKeyTestData key5, TranslatedTextValueTestData value5) {
        return MapOfTranslatedText(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4), entry(key5, value5));
    }

    public static void setHashFunction(ToIntFunction<MapKeyTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(HollowTestDataMapEntry<MapKeyTestData, TranslatedTextValueTestData> e : elements) {
            if(hashFunction == null)
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine));
            else
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine), hashFunction.applyAsInt(e.key()));
        }
        return rec;
    }

}