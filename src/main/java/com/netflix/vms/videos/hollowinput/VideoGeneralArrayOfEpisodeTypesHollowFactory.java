package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

public class VideoGeneralArrayOfEpisodeTypesHollowFactory<T extends VideoGeneralArrayOfEpisodeTypesHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoGeneralArrayOfEpisodeTypesHollow(((VideoGeneralArrayOfEpisodeTypesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoGeneralArrayOfEpisodeTypesHollow(new HollowListCachedDelegate((VideoGeneralArrayOfEpisodeTypesTypeAPI)typeAPI, ordinal), ordinal);
    }

}