package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class DownloadableIdListHollow extends HollowList<DownloadableIdHollow> {

    public DownloadableIdListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public DownloadableIdHollow instantiateElement(int ordinal) {
        return (DownloadableIdHollow) api().getDownloadableIdHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DownloadableIdListTypeAPI typeApi() {
        return (DownloadableIdListTypeAPI) delegate.getTypeAPI();
    }

}