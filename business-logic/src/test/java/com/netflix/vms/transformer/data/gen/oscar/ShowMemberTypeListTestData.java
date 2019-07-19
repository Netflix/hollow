package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowMemberTypeListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ShowMemberTypeList", "ShowMemberType");

    private final List<ShowMemberTypeTestData> elements = new ArrayList<>();

    public ShowMemberTypeListTestData(ShowMemberTypeTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ShowMemberTypeListTestData ShowMemberTypeList(ShowMemberTypeTestData... elements) {
        return new ShowMemberTypeListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(ShowMemberTypeTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}