package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

public class VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollowFactory<T extends VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow(((VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSHollow(new HollowListCachedDelegate((VideoArtWorkSourceAttributesArrayOfAWARD_CAMPAIGNSTypeAPI)typeAPI, ordinal), ordinal);
    }

}