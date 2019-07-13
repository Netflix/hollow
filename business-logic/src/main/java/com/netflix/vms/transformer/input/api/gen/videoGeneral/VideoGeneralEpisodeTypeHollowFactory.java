package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class VideoGeneralEpisodeTypeHollowFactory<T extends VideoGeneralEpisodeType> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoGeneralEpisodeType(((VideoGeneralEpisodeTypeTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoGeneralEpisodeType(new VideoGeneralEpisodeTypeDelegateCachedImpl((VideoGeneralEpisodeTypeTypeAPI)typeAPI, ordinal), ordinal);
    }

}