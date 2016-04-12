package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PackageDrmInfoListHollow extends HollowList<PackageDrmInfoHollow> {

    public PackageDrmInfoListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PackageDrmInfoHollow instantiateElement(int ordinal) {
        return (PackageDrmInfoHollow) api().getPackageDrmInfoHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageDrmInfoListTypeAPI typeApi() {
        return (PackageDrmInfoListTypeAPI) delegate.getTypeAPI();
    }

}