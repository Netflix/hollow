package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class PersonArtworkAttributesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, PersonArtworkAttributesDelegate {

    private final int file_seqOrdinal;
   private PersonArtworkAttributesTypeAPI typeAPI;

    public PersonArtworkAttributesDelegateCachedImpl(PersonArtworkAttributesTypeAPI typeAPI, int ordinal) {
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

    public PersonArtworkAttributesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (PersonArtworkAttributesTypeAPI) typeAPI;
    }

}