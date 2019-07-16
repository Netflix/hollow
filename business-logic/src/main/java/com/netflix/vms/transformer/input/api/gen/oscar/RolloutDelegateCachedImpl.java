package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class RolloutDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutDelegate {

    private final Long rolloutId;
    private final Long movieId;
    private final int movieIdOrdinal;
    private final String rolloutName;
    private final int rolloutNameOrdinal;
    private final String type;
    private final int typeOrdinal;
    private final String status;
    private final int statusOrdinal;
    private final int phasesOrdinal;
    private final int countriesOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private RolloutTypeAPI typeAPI;

    public RolloutDelegateCachedImpl(RolloutTypeAPI typeAPI, int ordinal) {
        this.rolloutId = typeAPI.getRolloutIdBoxed(ordinal);
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.rolloutNameOrdinal = typeAPI.getRolloutNameOrdinal(ordinal);
        int rolloutNameTempOrdinal = rolloutNameOrdinal;
        this.rolloutName = rolloutNameTempOrdinal == -1 ? null : typeAPI.getAPI().getRolloutNameTypeAPI().getValue(rolloutNameTempOrdinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        int typeTempOrdinal = typeOrdinal;
        this.type = typeTempOrdinal == -1 ? null : typeAPI.getAPI().getRolloutTypeTypeAPI().get_name(typeTempOrdinal);
        this.statusOrdinal = typeAPI.getStatusOrdinal(ordinal);
        int statusTempOrdinal = statusOrdinal;
        this.status = statusTempOrdinal == -1 ? null : typeAPI.getAPI().getRolloutStatusTypeAPI().get_name(statusTempOrdinal);
        this.phasesOrdinal = typeAPI.getPhasesOrdinal(ordinal);
        this.countriesOrdinal = typeAPI.getCountriesOrdinal(ordinal);
        this.dateCreatedOrdinal = typeAPI.getDateCreatedOrdinal(ordinal);
        int dateCreatedTempOrdinal = dateCreatedOrdinal;
        this.dateCreated = dateCreatedTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(dateCreatedTempOrdinal);
        this.lastUpdatedOrdinal = typeAPI.getLastUpdatedOrdinal(ordinal);
        int lastUpdatedTempOrdinal = lastUpdatedOrdinal;
        this.lastUpdated = lastUpdatedTempOrdinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValue(lastUpdatedTempOrdinal);
        this.createdByOrdinal = typeAPI.getCreatedByOrdinal(ordinal);
        int createdByTempOrdinal = createdByOrdinal;
        this.createdBy = createdByTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(createdByTempOrdinal);
        this.updatedByOrdinal = typeAPI.getUpdatedByOrdinal(ordinal);
        int updatedByTempOrdinal = updatedByOrdinal;
        this.updatedBy = updatedByTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(updatedByTempOrdinal);
        this.typeAPI = typeAPI;
    }

    public long getRolloutId(int ordinal) {
        if(rolloutId == null)
            return Long.MIN_VALUE;
        return rolloutId.longValue();
    }

    public Long getRolloutIdBoxed(int ordinal) {
        return rolloutId;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public String getRolloutName(int ordinal) {
        return rolloutName;
    }

    public boolean isRolloutNameEqual(int ordinal, String testValue) {
        if(testValue == null)
            return rolloutName == null;
        return testValue.equals(rolloutName);
    }

    public int getRolloutNameOrdinal(int ordinal) {
        return rolloutNameOrdinal;
    }

    public String getType(int ordinal) {
        return type;
    }

    public boolean isTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return type == null;
        return testValue.equals(type);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    public String getStatus(int ordinal) {
        return status;
    }

    public boolean isStatusEqual(int ordinal, String testValue) {
        if(testValue == null)
            return status == null;
        return testValue.equals(status);
    }

    public int getStatusOrdinal(int ordinal) {
        return statusOrdinal;
    }

    public int getPhasesOrdinal(int ordinal) {
        return phasesOrdinal;
    }

    public int getCountriesOrdinal(int ordinal) {
        return countriesOrdinal;
    }

    public long getDateCreated(int ordinal) {
        if(dateCreated == null)
            return Long.MIN_VALUE;
        return dateCreated.longValue();
    }

    public Long getDateCreatedBoxed(int ordinal) {
        return dateCreated;
    }

    public int getDateCreatedOrdinal(int ordinal) {
        return dateCreatedOrdinal;
    }

    public long getLastUpdated(int ordinal) {
        if(lastUpdated == null)
            return Long.MIN_VALUE;
        return lastUpdated.longValue();
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        return lastUpdated;
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        return lastUpdatedOrdinal;
    }

    public String getCreatedBy(int ordinal) {
        return createdBy;
    }

    public boolean isCreatedByEqual(int ordinal, String testValue) {
        if(testValue == null)
            return createdBy == null;
        return testValue.equals(createdBy);
    }

    public int getCreatedByOrdinal(int ordinal) {
        return createdByOrdinal;
    }

    public String getUpdatedBy(int ordinal) {
        return updatedBy;
    }

    public boolean isUpdatedByEqual(int ordinal, String testValue) {
        if(testValue == null)
            return updatedBy == null;
        return testValue.equals(updatedBy);
    }

    public int getUpdatedByOrdinal(int ordinal) {
        return updatedByOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutTypeAPI) typeAPI;
    }

}