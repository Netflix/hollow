package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class PackageStreamSetHollow extends HollowSet<PackageStreamHollow> {

    public PackageStreamSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PackageStreamHollow instantiateElement(int ordinal) {
        return (PackageStreamHollow) api().getPackageStreamHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageStreamSetTypeAPI typeApi() {
        return (PackageStreamSetTypeAPI) delegate.getTypeAPI();
    }

}