package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutPhaseTypeAPI extends HollowObjectTypeAPI {

    private final RolloutPhaseDelegateLookupImpl delegateLookupImpl;

    public RolloutPhaseTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "rolloutPhaseId",
            "phaseName",
            "startDate",
            "endDate",
            "windowType",
            "showCoreMetadata",
            "onHold",
            "seasonMovieId",
            "phaseType",
            "trailers",
            "castMembers",
            "metadataElements",
            "phaseArtworks",
            "requiredImageTypes",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new RolloutPhaseDelegateLookupImpl(this);
    }

    public long getRolloutPhaseId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RolloutPhase", ordinal, "rolloutPhaseId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getRolloutPhaseIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RolloutPhase", ordinal, "rolloutPhaseId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getPhaseNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "phaseName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public PhaseNameTypeAPI getPhaseNameTypeAPI() {
        return getAPI().getPhaseNameTypeAPI();
    }

    public int getStartDateOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "startDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public DateTypeAPI getStartDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getEndDateOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "endDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public DateTypeAPI getEndDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getWindowTypeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "windowType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public WindowTypeTypeAPI getWindowTypeTypeAPI() {
        return getAPI().getWindowTypeTypeAPI();
    }

    public boolean getShowCoreMetadata(int ordinal) {
        if(fieldIndex[5] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RolloutPhase", ordinal, "showCoreMetadata"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]));
    }

    public Boolean getShowCoreMetadataBoxed(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleBoolean("RolloutPhase", ordinal, "showCoreMetadata");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[5]);
    }



    public boolean getOnHold(int ordinal) {
        if(fieldIndex[6] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("RolloutPhase", ordinal, "onHold"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]));
    }

    public Boolean getOnHoldBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("RolloutPhase", ordinal, "onHold");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public int getSeasonMovieIdOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "seasonMovieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public MovieIdTypeAPI getSeasonMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getPhaseTypeOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "phaseType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public PhaseTypeTypeAPI getPhaseTypeTypeAPI() {
        return getAPI().getPhaseTypeTypeAPI();
    }

    public int getTrailersOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "trailers");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public SetOfPhaseTrailerTypeAPI getTrailersTypeAPI() {
        return getAPI().getSetOfPhaseTrailerTypeAPI();
    }

    public int getCastMembersOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "castMembers");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public SetOfPhaseCastMemberTypeAPI getCastMembersTypeAPI() {
        return getAPI().getSetOfPhaseCastMemberTypeAPI();
    }

    public int getMetadataElementsOrdinal(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "metadataElements");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[11]);
    }

    public SetOfPhaseMetadataElementTypeAPI getMetadataElementsTypeAPI() {
        return getAPI().getSetOfPhaseMetadataElementTypeAPI();
    }

    public int getPhaseArtworksOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "phaseArtworks");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public SetOfPhaseArtworkTypeAPI getPhaseArtworksTypeAPI() {
        return getAPI().getSetOfPhaseArtworkTypeAPI();
    }

    public int getRequiredImageTypesOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "requiredImageTypes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public SetOfPhaseRequiredImageTypeTypeAPI getRequiredImageTypesTypeAPI() {
        return getAPI().getSetOfPhaseRequiredImageTypeTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[15]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleReferencedOrdinal("RolloutPhase", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[17]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public RolloutPhaseDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}