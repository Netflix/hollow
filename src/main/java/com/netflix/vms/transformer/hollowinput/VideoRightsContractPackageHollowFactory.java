package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class VideoRightsContractPackageHollowFactory<T extends VideoRightsContractPackageHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsContractPackageHollow(((VideoRightsContractPackageTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new VideoRightsContractPackageHollow(new VideoRightsContractPackageDelegateCachedImpl((VideoRightsContractPackageTypeAPI)typeAPI, ordinal), ordinal);
    }

}