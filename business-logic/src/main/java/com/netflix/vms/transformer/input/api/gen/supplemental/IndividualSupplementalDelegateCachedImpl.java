package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IndividualSupplementalDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, IndividualSupplementalDelegate {

    private final Long movieId;
    private final Integer sequenceNumber;
    private final String subType;
    private final int subTypeOrdinal;
    private final int themesOrdinal;
    private final int identifiersOrdinal;
    private final int usagesOrdinal;
    private final Boolean postplay;
    private final Boolean general;
    private final Boolean thematic;
    private final Boolean approvedForExploit;
    private IndividualSupplementalTypeAPI typeAPI;

    public IndividualSupplementalDelegateCachedImpl(IndividualSupplementalTypeAPI typeAPI, int ordinal) {
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.subTypeOrdinal = typeAPI.getSubTypeOrdinal(ordinal);
        int subTypeTempOrdinal = subTypeOrdinal;
        this.subType = subTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(subTypeTempOrdinal);
        this.themesOrdinal = typeAPI.getThemesOrdinal(ordinal);
        this.identifiersOrdinal = typeAPI.getIdentifiersOrdinal(ordinal);
        this.usagesOrdinal = typeAPI.getUsagesOrdinal(ordinal);
        this.postplay = typeAPI.getPostplayBoxed(ordinal);
        this.general = typeAPI.getGeneralBoxed(ordinal);
        this.thematic = typeAPI.getThematicBoxed(ordinal);
        this.approvedForExploit = typeAPI.getApprovedForExploitBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getSequenceNumber(int ordinal) {
        if(sequenceNumber == null)
            return Integer.MIN_VALUE;
        return sequenceNumber.intValue();
    }

    public Integer getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public String getSubType(int ordinal) {
        return subType;
    }

    public boolean isSubTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return subType == null;
        return testValue.equals(subType);
    }

    public int getSubTypeOrdinal(int ordinal) {
        return subTypeOrdinal;
    }

    public int getThemesOrdinal(int ordinal) {
        return themesOrdinal;
    }

    public int getIdentifiersOrdinal(int ordinal) {
        return identifiersOrdinal;
    }

    public int getUsagesOrdinal(int ordinal) {
        return usagesOrdinal;
    }

    public boolean getPostplay(int ordinal) {
        if(postplay == null)
            return false;
        return postplay.booleanValue();
    }

    public Boolean getPostplayBoxed(int ordinal) {
        return postplay;
    }

    public boolean getGeneral(int ordinal) {
        if(general == null)
            return false;
        return general.booleanValue();
    }

    public Boolean getGeneralBoxed(int ordinal) {
        return general;
    }

    public boolean getThematic(int ordinal) {
        if(thematic == null)
            return false;
        return thematic.booleanValue();
    }

    public Boolean getThematicBoxed(int ordinal) {
        return thematic;
    }

    public boolean getApprovedForExploit(int ordinal) {
        if(approvedForExploit == null)
            return false;
        return approvedForExploit.booleanValue();
    }

    public Boolean getApprovedForExploitBoxed(int ordinal) {
        return approvedForExploit;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public IndividualSupplementalTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (IndividualSupplementalTypeAPI) typeAPI;
    }

}