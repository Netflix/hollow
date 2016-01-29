package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class Stories_SynopsesNarrativeTextTranslatedTextsHollowFactory<T extends Stories_SynopsesNarrativeTextTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesNarrativeTextTranslatedTextsHollow(((Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesNarrativeTextTranslatedTextsHollow(new Stories_SynopsesNarrativeTextTranslatedTextsDelegateCachedImpl((Stories_SynopsesNarrativeTextTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}