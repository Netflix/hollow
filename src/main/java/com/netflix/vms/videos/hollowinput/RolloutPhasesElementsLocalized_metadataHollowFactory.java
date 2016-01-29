package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class RolloutPhasesElementsLocalized_metadataHollowFactory<T extends RolloutPhasesElementsLocalized_metadataHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsLocalized_metadataHollow(((RolloutPhasesElementsLocalized_metadataTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsLocalized_metadataHollow(new RolloutPhasesElementsLocalized_metadataDelegateCachedImpl((RolloutPhasesElementsLocalized_metadataTypeAPI)typeAPI, ordinal), ordinal);
    }

}