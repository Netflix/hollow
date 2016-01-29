package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class Stories_SynopsesHooksTranslatedTextsHollowFactory<T extends Stories_SynopsesHooksTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesHooksTranslatedTextsHollow(((Stories_SynopsesHooksTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesHooksTranslatedTextsHollow(new Stories_SynopsesHooksTranslatedTextsDelegateCachedImpl((Stories_SynopsesHooksTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}