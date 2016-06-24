package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowSetCachedDelegate;

@SuppressWarnings("all")
public class VideoRightsContractSetHollowFactory<T extends VideoRightsContractSetHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsContractSetHollow(((VideoRightsContractSetTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsContractSetHollow(new HollowSetCachedDelegate((VideoRightsContractSetTypeAPI)typeAPI, ordinal), ordinal);
    }

}