package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class VideoArtWorkLocalesArrayOfTerritoryCodesHollow extends HollowList<VideoArtWorkLocalesTerritoryCodesHollow> {

    public VideoArtWorkLocalesArrayOfTerritoryCodesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VideoArtWorkLocalesTerritoryCodesHollow instantiateElement(int ordinal) {
        return (VideoArtWorkLocalesTerritoryCodesHollow) api().getVideoArtWorkLocalesTerritoryCodesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI typeApi() {
        return (VideoArtWorkLocalesArrayOfTerritoryCodesTypeAPI) delegate.getTypeAPI();
    }

}