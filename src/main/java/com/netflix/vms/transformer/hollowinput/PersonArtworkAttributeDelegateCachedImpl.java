package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class PersonArtworkAttributeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonArtworkAttributeDelegate {

    private final int file_seqOrdinal;
   private PersonArtworkAttributeTypeAPI typeAPI;

    public PersonArtworkAttributeDelegateCachedImpl(PersonArtworkAttributeTypeAPI typeAPI, int ordinal) {
        this.file_seqOrdinal = typeAPI.getFile_seqOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getFile_seqOrdinal(int ordinal) {
        return file_seqOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public PersonArtworkAttributeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonArtworkAttributeTypeAPI) typeAPI;
    }

}