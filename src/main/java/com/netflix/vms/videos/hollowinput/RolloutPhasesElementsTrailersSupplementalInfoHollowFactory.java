package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class RolloutPhasesElementsTrailersSupplementalInfoHollowFactory<T extends RolloutPhasesElementsTrailersSupplementalInfoHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsTrailersSupplementalInfoHollow(((RolloutPhasesElementsTrailersSupplementalInfoTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new RolloutPhasesElementsTrailersSupplementalInfoHollow(new RolloutPhasesElementsTrailersSupplementalInfoDelegateCachedImpl((RolloutPhasesElementsTrailersSupplementalInfoTypeAPI)typeAPI, ordinal), ordinal);
    }

}