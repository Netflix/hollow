package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class Stories_SynopsesNarrativeTextHollowFactory<T extends Stories_SynopsesNarrativeTextHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesNarrativeTextHollow(((Stories_SynopsesNarrativeTextTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesNarrativeTextHollow(new Stories_SynopsesNarrativeTextDelegateCachedImpl((Stories_SynopsesNarrativeTextTypeAPI)typeAPI, ordinal), ordinal);
    }

}