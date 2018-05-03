package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapCachedDelegate;

@SuppressWarnings("all")
public class MapOfTranslatedTextHollowFactory<T extends MapOfTranslatedTextHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MapOfTranslatedTextHollow(((MapOfTranslatedTextTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MapOfTranslatedTextHollow(new HollowMapCachedDelegate((MapOfTranslatedTextTypeAPI)typeAPI, ordinal), ordinal);
    }

}