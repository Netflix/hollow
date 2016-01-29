package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class TrailerTrailersArrayOfThemesHollow extends HollowList<TrailerTrailersThemesHollow> {

    public TrailerTrailersArrayOfThemesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TrailerTrailersThemesHollow instantiateElement(int ordinal) {
        return (TrailerTrailersThemesHollow) api().getTrailerTrailersThemesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailerTrailersArrayOfThemesTypeAPI typeApi() {
        return (TrailerTrailersArrayOfThemesTypeAPI) delegate.getTypeAPI();
    }

}