package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

public class VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollowFactory<T extends VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow(((VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesArrayOfPERSON_IDSHollow(new HollowListCachedDelegate((VideoArtWorkSourceAttributesArrayOfPERSON_IDSTypeAPI)typeAPI, ordinal), ordinal);
    }

}