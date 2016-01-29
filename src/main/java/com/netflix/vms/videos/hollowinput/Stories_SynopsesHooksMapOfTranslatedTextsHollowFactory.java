package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowMapCachedDelegate;

public class Stories_SynopsesHooksMapOfTranslatedTextsHollowFactory<T extends Stories_SynopsesHooksMapOfTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesHooksMapOfTranslatedTextsHollow(((Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new Stories_SynopsesHooksMapOfTranslatedTextsHollow(new HollowMapCachedDelegate((Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}