package com.netflix.vms.transformer.data.gen.exhibitDealAttribute;

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

public class SetOfDisallowedAssetBundleEntryTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("SetOfDisallowedAssetBundleEntry", "DisallowedAssetBundleEntry");

    private static ToIntFunction<DisallowedAssetBundleEntryTestData> hashFunction = null;

    private final List<DisallowedAssetBundleEntryTestData> elements = new ArrayList<>();

    public SetOfDisallowedAssetBundleEntryTestData(DisallowedAssetBundleEntryTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static SetOfDisallowedAssetBundleEntryTestData SetOfDisallowedAssetBundleEntry(DisallowedAssetBundleEntryTestData... elements) {
        return new SetOfDisallowedAssetBundleEntryTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<DisallowedAssetBundleEntryTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(DisallowedAssetBundleEntryTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}