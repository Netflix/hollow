package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

@SuppressWarnings("all")
public class VideoGeneralTitleTypeHollowFactory<T extends VideoGeneralTitleType> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoGeneralTitleType(((VideoGeneralTitleTypeTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoGeneralTitleType(new VideoGeneralTitleTypeDelegateCachedImpl((VideoGeneralTitleTypeTypeAPI)typeAPI, ordinal), ordinal);
    }

}