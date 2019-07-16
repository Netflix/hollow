package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieTitleAkaDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieTitleAkaDelegate {

    private final Long id;
    private final Long movieId;
    private final int movieIdOrdinal;
    private final String alias;
    private final int aliasOrdinal;
    private final String bcpCode;
    private final int bcpCodeOrdinal;
    private final String sourceType;
    private final int sourceTypeOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private MovieTitleAkaTypeAPI typeAPI;

    public MovieTitleAkaDelegateCachedImpl(MovieTitleAkaTypeAPI typeAPI, int ordinal) {
        this.id = typeAPI.getIdBoxed(ordinal);
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.aliasOrdinal = typeAPI.getAliasOrdinal(ordinal);
        int aliasTempOrdinal = aliasOrdinal;
        this.alias = aliasTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(aliasTempOrdinal);
        this.bcpCodeOrdinal = typeAPI.getBcpCodeOrdinal(ordinal);
        int bcpCodeTempOrdinal = bcpCodeOrdinal;
        this.bcpCode = bcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(bcpCodeTempOrdinal);
        this.sourceTypeOrdinal = typeAPI.getSourceTypeOrdinal(ordinal);
        int sourceTypeTempOrdinal = sourceTypeOrdinal;
        this.sourceType = sourceTypeTempOrdinal == -1 ? null : typeAPI.getAPI().getTitleSourceTypeTypeAPI().get_name(sourceTypeTempOrdinal);
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

    public long getId(int ordinal) {
        if(id == null)
            return Long.MIN_VALUE;
        return id.longValue();
    }

    public Long getIdBoxed(int ordinal) {
        return id;
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

    public String getAlias(int ordinal) {
        return alias;
    }

    public boolean isAliasEqual(int ordinal, String testValue) {
        if(testValue == null)
            return alias == null;
        return testValue.equals(alias);
    }

    public int getAliasOrdinal(int ordinal) {
        return aliasOrdinal;
    }

    public String getBcpCode(int ordinal) {
        return bcpCode;
    }

    public boolean isBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return bcpCode == null;
        return testValue.equals(bcpCode);
    }

    public int getBcpCodeOrdinal(int ordinal) {
        return bcpCodeOrdinal;
    }

    public String getSourceType(int ordinal) {
        return sourceType;
    }

    public boolean isSourceTypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return sourceType == null;
        return testValue.equals(sourceType);
    }

    public int getSourceTypeOrdinal(int ordinal) {
        return sourceTypeOrdinal;
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

    public MovieTitleAkaTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieTitleAkaTypeAPI) typeAPI;
    }

}