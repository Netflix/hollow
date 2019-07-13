package com.netflix.vms.transformer.data.gen.packageDealCountry;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfPackageTagsTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ListOfPackageTags", "String");

    private final List<StringTestData> elements = new ArrayList<>();

    public ListOfPackageTagsTestData(StringTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ListOfPackageTagsTestData ListOfPackageTags(StringTestData... elements) {
        return new ListOfPackageTagsTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(StringTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}