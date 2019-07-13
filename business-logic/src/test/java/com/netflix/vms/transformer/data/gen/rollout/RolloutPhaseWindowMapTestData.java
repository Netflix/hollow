package com.netflix.vms.transformer.data.gen.rollout;

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

public class RolloutPhaseWindowMapTestData extends HollowTestRecord {

    private static final HollowMapSchema SCHEMA = new HollowMapSchema("RolloutPhaseWindowMap", "ISOCountry", "RolloutPhaseWindow", "value");

    private static ToIntFunction<ISOCountryTestData> hashFunction = null;

    private final List<HollowTestDataMapEntry<ISOCountryTestData, RolloutPhaseWindowTestData>> elements = new ArrayList<>();

    @SafeVarargs
    public RolloutPhaseWindowMapTestData(HollowTestDataMapEntry<ISOCountryTestData, RolloutPhaseWindowTestData>... entries) {
        this.elements.addAll(Arrays.asList(entries));
    }

    @SafeVarargs
    public static RolloutPhaseWindowMapTestData RolloutPhaseWindowMap(HollowTestDataMapEntry<ISOCountryTestData, RolloutPhaseWindowTestData>... elements) {
        return new RolloutPhaseWindowMapTestData(elements);
    }

    public static RolloutPhaseWindowMapTestData RolloutPhaseWindowMap(ISOCountryTestData key, RolloutPhaseWindowTestData value) {
        return RolloutPhaseWindowMap(entry(key, value));
    }

    public static RolloutPhaseWindowMapTestData RolloutPhaseWindowMap(
            ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
            ISOCountryTestData key2, RolloutPhaseWindowTestData value2) {
        return RolloutPhaseWindowMap(entry(key1, value1), entry(key2, value2));
    }

    public static RolloutPhaseWindowMapTestData RolloutPhaseWindowMap(
            ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
            ISOCountryTestData key2, RolloutPhaseWindowTestData value2,
            ISOCountryTestData key3, RolloutPhaseWindowTestData value3) {
        return RolloutPhaseWindowMap(entry(key1, value1), entry(key2, value2), entry(key3, value3));
    }

    public static RolloutPhaseWindowMapTestData RolloutPhaseWindowMap(
            ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
            ISOCountryTestData key2, RolloutPhaseWindowTestData value2,
            ISOCountryTestData key3, RolloutPhaseWindowTestData value3,
            ISOCountryTestData key4, RolloutPhaseWindowTestData value4) {
        return RolloutPhaseWindowMap(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4));
    }

    public static RolloutPhaseWindowMapTestData RolloutPhaseWindowMap(
            ISOCountryTestData key1, RolloutPhaseWindowTestData value1,
            ISOCountryTestData key2, RolloutPhaseWindowTestData value2,
            ISOCountryTestData key3, RolloutPhaseWindowTestData value3,
            ISOCountryTestData key4, RolloutPhaseWindowTestData value4,
            ISOCountryTestData key5, RolloutPhaseWindowTestData value5) {
        return RolloutPhaseWindowMap(entry(key1, value1), entry(key2, value2), entry(key3, value3), entry(key4, value4), entry(key5, value5));
    }

    public static void setHashFunction(ToIntFunction<ISOCountryTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for(HollowTestDataMapEntry<ISOCountryTestData, RolloutPhaseWindowTestData> e : elements) {
            if(hashFunction == null)
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine));
            else
                rec.addEntry(e.key().addTo(writeEngine), e.value().addTo(writeEngine), hashFunction.applyAsInt(e.key()));
        }
        return rec;
    }

}