package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class DownloadableIdListHollow extends HollowList<DownloadableIdHollow> {

    public DownloadableIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DownloadableIdHollow instantiateElement(int ordinal) {
        return (DownloadableIdHollow) api().getDownloadableIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public DownloadableIdListTypeAPI typeApi() {
        return (DownloadableIdListTypeAPI) delegate.getTypeAPI();
    }

}