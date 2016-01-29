package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollowFactory<T extends VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow(((VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesAWARD_CAMPAIGNSHollow(new VideoArtWorkSourceAttributesAWARD_CAMPAIGNSDelegateCachedImpl((VideoArtWorkSourceAttributesAWARD_CAMPAIGNSTypeAPI)typeAPI, ordinal), ordinal);
    }

}