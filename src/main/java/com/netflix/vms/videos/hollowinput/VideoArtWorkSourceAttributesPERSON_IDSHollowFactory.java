package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class VideoArtWorkSourceAttributesPERSON_IDSHollowFactory<T extends VideoArtWorkSourceAttributesPERSON_IDSHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesPERSON_IDSHollow(((VideoArtWorkSourceAttributesPERSON_IDSTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoArtWorkSourceAttributesPERSON_IDSHollow(new VideoArtWorkSourceAttributesPERSON_IDSDelegateCachedImpl((VideoArtWorkSourceAttributesPERSON_IDSTypeAPI)typeAPI, ordinal), ordinal);
    }

}