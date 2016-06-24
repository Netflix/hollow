package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdHollowFactory<T extends RolloutPhaseArtworkSourceFileIdHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhaseArtworkSourceFileIdHollow(((RolloutPhaseArtworkSourceFileIdTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhaseArtworkSourceFileIdHollow(new RolloutPhaseArtworkSourceFileIdDelegateCachedImpl((RolloutPhaseArtworkSourceFileIdTypeAPI)typeAPI, ordinal), ordinal);
    }

}