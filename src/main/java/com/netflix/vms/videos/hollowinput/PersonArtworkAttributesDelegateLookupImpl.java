package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkAttributesDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonArtworkAttributesDelegate {

    private final PersonArtworkAttributesTypeAPI typeAPI;

    public PersonArtworkAttributesDelegateLookupImpl(PersonArtworkAttributesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getFile_seqOrdinal(int ordinal) {
        return typeAPI.getFile_seqOrdinal(ordinal);
    }

    public PersonArtworkAttributesTypeAPI getTypeAPI() {
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