package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TrailerArrayOfTrailersHollow extends HollowList<TrailerTrailersHollow> {

    public TrailerArrayOfTrailersHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TrailerTrailersHollow instantiateElement(int ordinal) {
        return (TrailerTrailersHollow) api().getTrailerTrailersHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailerArrayOfTrailersTypeAPI typeApi() {
        return (TrailerArrayOfTrailersTypeAPI) delegate.getTypeAPI();
    }

}