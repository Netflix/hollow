package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowSetCachedDelegate;

public class VideoRightsContractAssetsSetHollowFactory<T extends VideoRightsContractAssetsSetHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsContractAssetsSetHollow(((VideoRightsContractAssetsSetTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsContractAssetsSetHollow(new HollowSetCachedDelegate((VideoRightsContractAssetsSetTypeAPI)typeAPI, ordinal), ordinal);
    }

}