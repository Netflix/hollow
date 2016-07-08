package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowSetCachedDelegate;

@SuppressWarnings("all")
public class VideoRightsWindowsSetHollowFactory<T extends VideoRightsWindowsSetHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsWindowsSetHollow(((VideoRightsWindowsSetTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsWindowsSetHollow(new HollowSetCachedDelegate((VideoRightsWindowsSetTypeAPI)typeAPI, ordinal), ordinal);
    }

}