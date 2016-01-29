package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class RolloutPhasesElementsArtwork_newSourceFileIdsHollowFactory<T extends RolloutPhasesElementsArtwork_newSourceFileIdsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsArtwork_newSourceFileIdsHollow(((RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsArtwork_newSourceFileIdsHollow(new RolloutPhasesElementsArtwork_newSourceFileIdsDelegateCachedImpl((RolloutPhasesElementsArtwork_newSourceFileIdsTypeAPI)typeAPI, ordinal), ordinal);
    }

}