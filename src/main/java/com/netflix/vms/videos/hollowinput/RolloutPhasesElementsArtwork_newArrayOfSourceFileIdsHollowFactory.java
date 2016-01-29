package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowListCachedDelegate;

public class RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollowFactory<T extends RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow(((RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsHollow(new HollowListCachedDelegate((RolloutPhasesElementsArtwork_newArrayOfSourceFileIdsTypeAPI)typeAPI, ordinal), ordinal);
    }

}