package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowMapCachedDelegate;

public class LocalizedCharacterMapOfTranslatedTextsHollowFactory<T extends LocalizedCharacterMapOfTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new LocalizedCharacterMapOfTranslatedTextsHollow(((LocalizedCharacterMapOfTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new LocalizedCharacterMapOfTranslatedTextsHollow(new HollowMapCachedDelegate((LocalizedCharacterMapOfTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}