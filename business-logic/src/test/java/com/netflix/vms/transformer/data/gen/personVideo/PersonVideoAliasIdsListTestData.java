package com.netflix.vms.transformer.data.gen.personVideo;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonVideoAliasIdsListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("PersonVideoAliasIdsList", "PersonVideoAliasId");

    private final List<PersonVideoAliasIdTestData> elements = new ArrayList<>();

    public PersonVideoAliasIdsListTestData(PersonVideoAliasIdTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static PersonVideoAliasIdsListTestData PersonVideoAliasIdsList(PersonVideoAliasIdTestData... elements) {
        return new PersonVideoAliasIdsListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(PersonVideoAliasIdTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}