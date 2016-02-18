package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkAttributeDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonArtworkAttributeDelegate {

    private final PersonArtworkAttributeTypeAPI typeAPI;

    public PersonArtworkAttributeDelegateLookupImpl(PersonArtworkAttributeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getFile_seqOrdinal(int ordinal) {
        return typeAPI.getFile_seqOrdinal(ordinal);
    }

    public PersonArtworkAttributeTypeAPI getTypeAPI() {
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