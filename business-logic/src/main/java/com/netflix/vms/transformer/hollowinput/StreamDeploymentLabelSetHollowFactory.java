package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowSetCachedDelegate;

@SuppressWarnings("all")
public class StreamDeploymentLabelSetHollowFactory<T extends StreamDeploymentLabelSetHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new StreamDeploymentLabelSetHollow(((StreamDeploymentLabelSetTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new StreamDeploymentLabelSetHollow(new HollowSetCachedDelegate((StreamDeploymentLabelSetTypeAPI)typeAPI, ordinal), ordinal);
    }

}