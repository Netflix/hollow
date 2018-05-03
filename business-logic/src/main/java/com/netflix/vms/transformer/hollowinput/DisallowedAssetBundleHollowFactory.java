package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;

@SuppressWarnings("all")
public class DisallowedAssetBundleHollowFactory<T extends DisallowedAssetBundleHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new DisallowedAssetBundleHollow(((DisallowedAssetBundleTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new DisallowedAssetBundleHollow(new DisallowedAssetBundleDelegateCachedImpl((DisallowedAssetBundleTypeAPI)typeAPI, ordinal), ordinal);
    }

}