package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class RolloutPhasesElementsArtwork_newHollowFactory<T extends RolloutPhasesElementsArtwork_newHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsArtwork_newHollow(((RolloutPhasesElementsArtwork_newTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsArtwork_newHollow(new RolloutPhasesElementsArtwork_newDelegateCachedImpl((RolloutPhasesElementsArtwork_newTypeAPI)typeAPI, ordinal), ordinal);
    }

}