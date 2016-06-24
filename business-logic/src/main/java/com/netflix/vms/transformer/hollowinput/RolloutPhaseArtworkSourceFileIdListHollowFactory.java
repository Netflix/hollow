package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdListHollowFactory<T extends RolloutPhaseArtworkSourceFileIdListHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhaseArtworkSourceFileIdListHollow(((RolloutPhaseArtworkSourceFileIdListTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhaseArtworkSourceFileIdListHollow(new HollowListCachedDelegate((RolloutPhaseArtworkSourceFileIdListTypeAPI)typeAPI, ordinal), ordinal);
    }

}