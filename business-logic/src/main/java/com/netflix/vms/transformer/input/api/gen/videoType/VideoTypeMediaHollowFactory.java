package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class VideoTypeMediaHollowFactory<T extends VideoTypeMedia> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoTypeMedia(((VideoTypeMediaTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoTypeMedia(new VideoTypeMediaDelegateCachedImpl((VideoTypeMediaTypeAPI)typeAPI, ordinal), ordinal);
    }

}