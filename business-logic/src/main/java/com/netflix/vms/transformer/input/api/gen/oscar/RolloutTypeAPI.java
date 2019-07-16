package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class RolloutTypeAPI extends HollowObjectTypeAPI {

    private final RolloutDelegateLookupImpl delegateLookupImpl;

    public RolloutTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "rolloutId",
            "movieId",
            "rolloutName",
            "type",
            "status",
            "phases",
            "countries",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new RolloutDelegateLookupImpl(this);
    }

    public long getRolloutId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Rollout", ordinal, "rolloutId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getRolloutIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Rollout", ordinal, "rolloutId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getRolloutNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "rolloutName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public RolloutNameTypeAPI getRolloutNameTypeAPI() {
        return getAPI().getRolloutNameTypeAPI();
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public RolloutTypeTypeAPI getTypeTypeAPI() {
        return getAPI().getRolloutTypeTypeAPI();
    }

    public int getStatusOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "status");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public RolloutStatusTypeAPI getStatusTypeAPI() {
        return getAPI().getRolloutStatusTypeAPI();
    }

    public int getPhasesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "phases");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public SetOfRolloutPhaseTypeAPI getPhasesTypeAPI() {
        return getAPI().getSetOfRolloutPhaseTypeAPI();
    }

    public int getCountriesOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "countries");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public SetOfRolloutCountryTypeAPI getCountriesTypeAPI() {
        return getAPI().getSetOfRolloutCountryTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleReferencedOrdinal("Rollout", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[10]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public RolloutDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}