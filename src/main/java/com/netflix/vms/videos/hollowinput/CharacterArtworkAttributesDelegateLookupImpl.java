package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CharacterArtworkAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements CharacterArtworkAttributesDelegate {

    private final CharacterArtworkAttributesTypeAPI typeAPI;

    public CharacterArtworkAttributesDelegateLookupImpl(CharacterArtworkAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getFile_seqOrdinal(int ordinal) {
        return typeAPI.getFile_seqOrdinal(ordinal);
    }

    public CharacterArtworkAttributesTypeAPI getTypeAPI() {
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