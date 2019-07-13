package com.netflix.vms.transformer.data.gen.showCountryLabel;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ISOCountryListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ISOCountryList", "ISOCountry");

    private final List<ISOCountryTestData> elements = new ArrayList<>();

    public ISOCountryListTestData(ISOCountryTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ISOCountryListTestData ISOCountryList(ISOCountryTestData... elements) {
        return new ISOCountryListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(ISOCountryTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}