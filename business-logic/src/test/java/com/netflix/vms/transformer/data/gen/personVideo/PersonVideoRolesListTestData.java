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

public class PersonVideoRolesListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("PersonVideoRolesList", "PersonVideoRole");

    private final List<PersonVideoRoleTestData> elements = new ArrayList<>();

    public PersonVideoRolesListTestData(PersonVideoRoleTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static PersonVideoRolesListTestData PersonVideoRolesList(PersonVideoRoleTestData... elements) {
        return new PersonVideoRolesListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(PersonVideoRoleTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}