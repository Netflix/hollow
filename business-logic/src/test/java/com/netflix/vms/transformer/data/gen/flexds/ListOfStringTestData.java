package com.netflix.vms.transformer.data.gen.flexds;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfStringTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ListOfString", "String");

    private final List<StringTestData> elements = new ArrayList<>();

    public ListOfStringTestData(StringTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ListOfStringTestData ListOfString(StringTestData... elements) {
        return new ListOfStringTestData(elements);
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