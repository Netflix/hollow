package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkLocalesTerritoryCodesDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterArtworkLocalesTerritoryCodesDelegate {

    private final CharacterArtworkLocalesTerritoryCodesTypeAPI typeAPI;

    public CharacterArtworkLocalesTerritoryCodesDelegateLookupImpl(CharacterArtworkLocalesTerritoryCodesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public CharacterArtworkLocalesTerritoryCodesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}