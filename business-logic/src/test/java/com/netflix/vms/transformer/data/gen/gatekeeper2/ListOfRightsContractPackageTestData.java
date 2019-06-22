package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfRightsContractPackageTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("ListOfRightsContractPackage", "RightsContractPackage");

    private final List<RightsContractPackageTestData> elements = new ArrayList<>();

    public ListOfRightsContractPackageTestData(RightsContractPackageTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static ListOfRightsContractPackageTestData ListOfRightsContractPackage(RightsContractPackageTestData... elements) {
        return new ListOfRightsContractPackageTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(RightsContractPackageTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}