package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class PersonArtworkDelegateLookupImpl extends HollowObjectAbstractDelegate implements PersonArtworkDelegate {

    private final PersonArtworkTypeAPI typeAPI;

    public PersonArtworkDelegateLookupImpl(PersonArtworkTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getDerivativesOrdinal(int ordinal) {
        return typeAPI.getDerivativesOrdinal(ordinal);
    }

    public int getLocalesOrdinal(int ordinal) {
        return typeAPI.getLocalesOrdinal(ordinal);
    }

    public long getSeqNum(int ordinal) {
        return typeAPI.getSeqNum(ordinal);
    }

    public Long getSeqNumBoxed(int ordinal) {
        return typeAPI.getSeqNumBoxed(ordinal);
    }

    public long getOrdinalPriority(int ordinal) {
        return typeAPI.getOrdinalPriority(ordinal);
    }

    public Long getOrdinalPriorityBoxed(int ordinal) {
        return typeAPI.getOrdinalPriorityBoxed(ordinal);
    }

    public int getSourceFileIdOrdinal(int ordinal) {
        return typeAPI.getSourceFileIdOrdinal(ordinal);
    }

    public int getAttributesOrdinal(int ordinal) {
        return typeAPI.getAttributesOrdinal(ordinal);
    }

    public long getPersonId(int ordinal) {
        return typeAPI.getPersonId(ordinal);
    }

    public Long getPersonIdBoxed(int ordinal) {
        return typeAPI.getPersonIdBoxed(ordinal);
    }

    public PersonArtworkTypeAPI getTypeAPI() {
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