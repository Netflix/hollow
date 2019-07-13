package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IndividualSupplementalDelegateLookupImpl extends HollowObjectAbstractDelegate implements IndividualSupplementalDelegate {

    private final IndividualSupplementalTypeAPI typeAPI;

    public IndividualSupplementalDelegateLookupImpl(IndividualSupplementalTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public String getSubType(int ordinal) {
        ordinal = typeAPI.getSubTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isSubTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSubTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getSubTypeOrdinal(int ordinal) {
        return typeAPI.getSubTypeOrdinal(ordinal);
    }

    public int getThemesOrdinal(int ordinal) {
        return typeAPI.getThemesOrdinal(ordinal);
    }

    public int getIdentifiersOrdinal(int ordinal) {
        return typeAPI.getIdentifiersOrdinal(ordinal);
    }

    public int getUsagesOrdinal(int ordinal) {
        return typeAPI.getUsagesOrdinal(ordinal);
    }

    public boolean getPostplay(int ordinal) {
        return typeAPI.getPostplay(ordinal);
    }

    public Boolean getPostplayBoxed(int ordinal) {
        return typeAPI.getPostplayBoxed(ordinal);
    }

    public boolean getGeneral(int ordinal) {
        return typeAPI.getGeneral(ordinal);
    }

    public Boolean getGeneralBoxed(int ordinal) {
        return typeAPI.getGeneralBoxed(ordinal);
    }

    public boolean getThematic(int ordinal) {
        return typeAPI.getThematic(ordinal);
    }

    public Boolean getThematicBoxed(int ordinal) {
        return typeAPI.getThematicBoxed(ordinal);
    }

    public boolean getApprovedForExploit(int ordinal) {
        return typeAPI.getApprovedForExploit(ordinal);
    }

    public Boolean getApprovedForExploitBoxed(int ordinal) {
        return typeAPI.getApprovedForExploitBoxed(ordinal);
    }

    public IndividualSupplementalTypeAPI getTypeAPI() {
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