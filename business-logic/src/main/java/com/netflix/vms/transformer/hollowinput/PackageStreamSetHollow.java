package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class PackageStreamSetHollow extends HollowSet<PackageStreamHollow> {

    public PackageStreamSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PackageStreamHollow instantiateElement(int ordinal) {
        return (PackageStreamHollow) api().getPackageStreamHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public PackageStreamSetTypeAPI typeApi() {
        return (PackageStreamSetTypeAPI) delegate.getTypeAPI();
    }

}