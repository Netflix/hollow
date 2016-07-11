package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class PackageMomentListHollow extends HollowList<PackageMomentHollow> {

    public PackageMomentListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PackageMomentHollow instantiateElement(int ordinal) {
        return (PackageMomentHollow) api().getPackageMomentHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageMomentListTypeAPI typeApi() {
        return (PackageMomentListTypeAPI) delegate.getTypeAPI();
    }

}