package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class CharacterArtworkLocalesTerritoryCodesHollowFactory<T extends CharacterArtworkLocalesTerritoryCodesHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new CharacterArtworkLocalesTerritoryCodesHollow(((CharacterArtworkLocalesTerritoryCodesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new CharacterArtworkLocalesTerritoryCodesHollow(new CharacterArtworkLocalesTerritoryCodesDelegateCachedImpl((CharacterArtworkLocalesTerritoryCodesTypeAPI)typeAPI, ordinal), ordinal);
    }

}