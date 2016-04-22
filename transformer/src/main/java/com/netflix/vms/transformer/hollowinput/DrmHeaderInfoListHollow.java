package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class DrmHeaderInfoListHollow extends HollowList<DrmHeaderInfoHollow> {

    public DrmHeaderInfoListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DrmHeaderInfoHollow instantiateElement(int ordinal) {
        return (DrmHeaderInfoHollow) api().getDrmHeaderInfoHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DrmHeaderInfoListTypeAPI typeApi() {
        return (DrmHeaderInfoListTypeAPI) delegate.getTypeAPI();
    }

}