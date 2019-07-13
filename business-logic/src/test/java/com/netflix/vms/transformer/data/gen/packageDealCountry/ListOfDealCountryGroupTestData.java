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

public class ListOfDealCountryGroupTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ListOfDealCountryGroup", "DealCountryGroup");

    private final List<DealCountryGroupTestData> elements = new ArrayList<>();

    public ListOfDealCountryGroupTestData(DealCountryGroupTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ListOfDealCountryGroupTestData ListOfDealCountryGroup(DealCountryGroupTestData... elements) {
        return new ListOfDealCountryGroupTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(DealCountryGroupTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}