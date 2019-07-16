package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieTitleNLSDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieTitleNLSDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final String type;
    private final int typeOrdinal;
    private final String titleText;
    private final int titleTextOrdinal;
    private final String merchBcpCode;
    private final int merchBcpCodeOrdinal;
    private final String titleBcpCode;
    private final int titleBcpCodeOrdinal;
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
    private MovieTitleNLSTypeAPI typeAPI;

    public MovieTitleNLSDelegateCachedImpl(MovieTitleNLSTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        int typeTempOrdinal = typeOrdinal;
        this.type = typeTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleTypeTypeAPI().get_name(typeTempOrdinal);
        this.titleTextOrdinal = typeAPI.getTitleTextOrdinal(ordinal);
        int titleTextTempOrdinal = titleTextOrdinal;
        this.titleText = titleTextTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(titleTextTempOrdinal);
        this.merchBcpCodeOrdinal = typeAPI.getMerchBcpCodeOrdinal(ordinal);
        int merchBcpCodeTempOrdinal = merchBcpCodeOrdinal;
        this.merchBcpCode = merchBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(merchBcpCodeTempOrdinal);
        this.titleBcpCodeOrdinal = typeAPI.getTitleBcpCodeOrdinal(ordinal);
        int titleBcpCodeTempOrdinal = titleBcpCodeOrdinal;
        this.titleBcpCode = titleBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(titleBcpCodeTempOrdinal);
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

    public String getTitleText(int ordinal) {
        return titleText;
    }

    public boolean isTitleTextEqual(int ordinal, String testValue) {
        if(testValue == null)
            return titleText == null;
        return testValue.equals(titleText);
    }

    public int getTitleTextOrdinal(int ordinal) {
        return titleTextOrdinal;
    }

    public String getMerchBcpCode(int ordinal) {
        return merchBcpCode;
    }

    public boolean isMerchBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return merchBcpCode == null;
        return testValue.equals(merchBcpCode);
    }

    public int getMerchBcpCodeOrdinal(int ordinal) {
        return merchBcpCodeOrdinal;
    }

    public String getTitleBcpCode(int ordinal) {
        return titleBcpCode;
    }

    public boolean isTitleBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return titleBcpCode == null;
        return testValue.equals(titleBcpCode);
    }

    public int getTitleBcpCodeOrdinal(int ordinal) {
        return titleBcpCodeOrdinal;
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

    public MovieTitleNLSTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieTitleNLSTypeAPI) typeAPI;
    }

}