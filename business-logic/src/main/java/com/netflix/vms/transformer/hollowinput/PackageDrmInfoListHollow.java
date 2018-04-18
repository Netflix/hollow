package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class PackageDrmInfoListHollow extends HollowList<PackageDrmInfoHollow> {

    public PackageDrmInfoListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PackageDrmInfoHollow instantiateElement(int ordinal) {
        return (PackageDrmInfoHollow) api().getPackageDrmInfoHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageDrmInfoListTypeAPI typeApi() {
        return (PackageDrmInfoListTypeAPI) delegate.getTypeAPI();
    }

}