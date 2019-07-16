package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class MovieDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, MovieDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final int typeOrdinal;
    private final String originalLanguageBcpCode;
    private final int originalLanguageBcpCodeOrdinal;
    private final String originalTitle;
    private final int originalTitleOrdinal;
    private final String originalTitleBcpCode;
    private final int originalTitleBcpCodeOrdinal;
    private final String originalTitleConcat;
    private final int originalTitleConcatOrdinal;
    private final String originalTitleConcatBcpCode;
    private final int originalTitleConcatBcpCodeOrdinal;
    private final String countryOfOrigin;
    private final int countryOfOriginOrdinal;
    private final Integer runLenth;
    private final Boolean availableInPlastic;
    private final Integer firstReleaseYear;
    private final Boolean active;
    private final Boolean tv;
    private final String comment;
    private final int commentOrdinal;
    private final Boolean original;
    private final String subtype;
    private final int subtypeOrdinal;
    private final Boolean testTitle;
    private final Integer metadataReleaseDays;
    private final Boolean manualMetadataReleaseDaysUpdate;
    private final String internalTitle;
    private final int internalTitleOrdinal;
    private final String internalTitleBcpCode;
    private final int internalTitleBcpCodeOrdinal;
    private final String internalTitlePart;
    private final int internalTitlePartOrdinal;
    private final String internalTitlePartBcpCode;
    private final int internalTitlePartBcpCodeOrdinal;
    private final String searchTitle;
    private final int searchTitleOrdinal;
    private final String director;
    private final int directorOrdinal;
    private final String creator;
    private final int creatorOrdinal;
    private final String forceReason;
    private final int forceReasonOrdinal;
    private final Boolean visible;
    private final String createdByTeam;
    private final int createdByTeamOrdinal;
    private final String updatedByTeam;
    private final int updatedByTeamOrdinal;
    private final Long dateCreated;
    private final int dateCreatedOrdinal;
    private final Long lastUpdated;
    private final int lastUpdatedOrdinal;
    private final String createdBy;
    private final int createdByOrdinal;
    private final String updatedBy;
    private final int updatedByOrdinal;
    private MovieTypeAPI typeAPI;

    public MovieDelegateCachedImpl(MovieTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValue(movieIdTempOrdinal);
        this.typeOrdinal = typeAPI.getTypeOrdinal(ordinal);
        this.originalLanguageBcpCodeOrdinal = typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
        int originalLanguageBcpCodeTempOrdinal = originalLanguageBcpCodeOrdinal;
        this.originalLanguageBcpCode = originalLanguageBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(originalLanguageBcpCodeTempOrdinal);
        this.originalTitleOrdinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        int originalTitleTempOrdinal = originalTitleOrdinal;
        this.originalTitle = originalTitleTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(originalTitleTempOrdinal);
        this.originalTitleBcpCodeOrdinal = typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
        int originalTitleBcpCodeTempOrdinal = originalTitleBcpCodeOrdinal;
        this.originalTitleBcpCode = originalTitleBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(originalTitleBcpCodeTempOrdinal);
        this.originalTitleConcatOrdinal = typeAPI.getOriginalTitleConcatOrdinal(ordinal);
        int originalTitleConcatTempOrdinal = originalTitleConcatOrdinal;
        this.originalTitleConcat = originalTitleConcatTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(originalTitleConcatTempOrdinal);
        this.originalTitleConcatBcpCodeOrdinal = typeAPI.getOriginalTitleConcatBcpCodeOrdinal(ordinal);
        int originalTitleConcatBcpCodeTempOrdinal = originalTitleConcatBcpCodeOrdinal;
        this.originalTitleConcatBcpCode = originalTitleConcatBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(originalTitleConcatBcpCodeTempOrdinal);
        this.countryOfOriginOrdinal = typeAPI.getCountryOfOriginOrdinal(ordinal);
        int countryOfOriginTempOrdinal = countryOfOriginOrdinal;
        this.countryOfOrigin = countryOfOriginTempOrdinal == -1 ? null : typeAPI.getAPI().getCountryStringTypeAPI().getValue(countryOfOriginTempOrdinal);
        this.runLenth = typeAPI.getRunLenthBoxed(ordinal);
        this.availableInPlastic = typeAPI.getAvailableInPlasticBoxed(ordinal);
        this.firstReleaseYear = typeAPI.getFirstReleaseYearBoxed(ordinal);
        this.active = typeAPI.getActiveBoxed(ordinal);
        this.tv = typeAPI.getTvBoxed(ordinal);
        this.commentOrdinal = typeAPI.getCommentOrdinal(ordinal);
        int commentTempOrdinal = commentOrdinal;
        this.comment = commentTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(commentTempOrdinal);
        this.original = typeAPI.getOriginalBoxed(ordinal);
        this.subtypeOrdinal = typeAPI.getSubtypeOrdinal(ordinal);
        int subtypeTempOrdinal = subtypeOrdinal;
        this.subtype = subtypeTempOrdinal == -1 ? null : typeAPI.getAPI().getSupplementalSubtypeTypeAPI().getValue(subtypeTempOrdinal);
        this.testTitle = typeAPI.getTestTitleBoxed(ordinal);
        this.metadataReleaseDays = typeAPI.getMetadataReleaseDaysBoxed(ordinal);
        this.manualMetadataReleaseDaysUpdate = typeAPI.getManualMetadataReleaseDaysUpdateBoxed(ordinal);
        this.internalTitleOrdinal = typeAPI.getInternalTitleOrdinal(ordinal);
        int internalTitleTempOrdinal = internalTitleOrdinal;
        this.internalTitle = internalTitleTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(internalTitleTempOrdinal);
        this.internalTitleBcpCodeOrdinal = typeAPI.getInternalTitleBcpCodeOrdinal(ordinal);
        int internalTitleBcpCodeTempOrdinal = internalTitleBcpCodeOrdinal;
        this.internalTitleBcpCode = internalTitleBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(internalTitleBcpCodeTempOrdinal);
        this.internalTitlePartOrdinal = typeAPI.getInternalTitlePartOrdinal(ordinal);
        int internalTitlePartTempOrdinal = internalTitlePartOrdinal;
        this.internalTitlePart = internalTitlePartTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(internalTitlePartTempOrdinal);
        this.internalTitlePartBcpCodeOrdinal = typeAPI.getInternalTitlePartBcpCodeOrdinal(ordinal);
        int internalTitlePartBcpCodeTempOrdinal = internalTitlePartBcpCodeOrdinal;
        this.internalTitlePartBcpCode = internalTitlePartBcpCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(internalTitlePartBcpCodeTempOrdinal);
        this.searchTitleOrdinal = typeAPI.getSearchTitleOrdinal(ordinal);
        int searchTitleTempOrdinal = searchTitleOrdinal;
        this.searchTitle = searchTitleTempOrdinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(searchTitleTempOrdinal);
        this.directorOrdinal = typeAPI.getDirectorOrdinal(ordinal);
        int directorTempOrdinal = directorOrdinal;
        this.director = directorTempOrdinal == -1 ? null : typeAPI.getAPI().getPersonNameTypeAPI().getValue(directorTempOrdinal);
        this.creatorOrdinal = typeAPI.getCreatorOrdinal(ordinal);
        int creatorTempOrdinal = creatorOrdinal;
        this.creator = creatorTempOrdinal == -1 ? null : typeAPI.getAPI().getPersonNameTypeAPI().getValue(creatorTempOrdinal);
        this.forceReasonOrdinal = typeAPI.getForceReasonOrdinal(ordinal);
        int forceReasonTempOrdinal = forceReasonOrdinal;
        this.forceReason = forceReasonTempOrdinal == -1 ? null : typeAPI.getAPI().getForceReasonTypeAPI().getValue(forceReasonTempOrdinal);
        this.visible = typeAPI.getVisibleBoxed(ordinal);
        this.createdByTeamOrdinal = typeAPI.getCreatedByTeamOrdinal(ordinal);
        int createdByTeamTempOrdinal = createdByTeamOrdinal;
        this.createdByTeam = createdByTeamTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(createdByTeamTempOrdinal);
        this.updatedByTeamOrdinal = typeAPI.getUpdatedByTeamOrdinal(ordinal);
        int updatedByTeamTempOrdinal = updatedByTeamOrdinal;
        this.updatedByTeam = updatedByTeamTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(updatedByTeamTempOrdinal);
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

    public int getTypeOrdinal(int ordinal) {
        return typeOrdinal;
    }

    public String getOriginalLanguageBcpCode(int ordinal) {
        return originalLanguageBcpCode;
    }

    public boolean isOriginalLanguageBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalLanguageBcpCode == null;
        return testValue.equals(originalLanguageBcpCode);
    }

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        return originalLanguageBcpCodeOrdinal;
    }

    public String getOriginalTitle(int ordinal) {
        return originalTitle;
    }

    public boolean isOriginalTitleEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalTitle == null;
        return testValue.equals(originalTitle);
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        return originalTitleOrdinal;
    }

    public String getOriginalTitleBcpCode(int ordinal) {
        return originalTitleBcpCode;
    }

    public boolean isOriginalTitleBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalTitleBcpCode == null;
        return testValue.equals(originalTitleBcpCode);
    }

    public int getOriginalTitleBcpCodeOrdinal(int ordinal) {
        return originalTitleBcpCodeOrdinal;
    }

    public String getOriginalTitleConcat(int ordinal) {
        return originalTitleConcat;
    }

    public boolean isOriginalTitleConcatEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalTitleConcat == null;
        return testValue.equals(originalTitleConcat);
    }

    public int getOriginalTitleConcatOrdinal(int ordinal) {
        return originalTitleConcatOrdinal;
    }

    public String getOriginalTitleConcatBcpCode(int ordinal) {
        return originalTitleConcatBcpCode;
    }

    public boolean isOriginalTitleConcatBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return originalTitleConcatBcpCode == null;
        return testValue.equals(originalTitleConcatBcpCode);
    }

    public int getOriginalTitleConcatBcpCodeOrdinal(int ordinal) {
        return originalTitleConcatBcpCodeOrdinal;
    }

    public String getCountryOfOrigin(int ordinal) {
        return countryOfOrigin;
    }

    public boolean isCountryOfOriginEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryOfOrigin == null;
        return testValue.equals(countryOfOrigin);
    }

    public int getCountryOfOriginOrdinal(int ordinal) {
        return countryOfOriginOrdinal;
    }

    public int getRunLenth(int ordinal) {
        if(runLenth == null)
            return Integer.MIN_VALUE;
        return runLenth.intValue();
    }

    public Integer getRunLenthBoxed(int ordinal) {
        return runLenth;
    }

    public boolean getAvailableInPlastic(int ordinal) {
        if(availableInPlastic == null)
            return false;
        return availableInPlastic.booleanValue();
    }

    public Boolean getAvailableInPlasticBoxed(int ordinal) {
        return availableInPlastic;
    }

    public int getFirstReleaseYear(int ordinal) {
        if(firstReleaseYear == null)
            return Integer.MIN_VALUE;
        return firstReleaseYear.intValue();
    }

    public Integer getFirstReleaseYearBoxed(int ordinal) {
        return firstReleaseYear;
    }

    public boolean getActive(int ordinal) {
        if(active == null)
            return false;
        return active.booleanValue();
    }

    public Boolean getActiveBoxed(int ordinal) {
        return active;
    }

    public boolean getTv(int ordinal) {
        if(tv == null)
            return false;
        return tv.booleanValue();
    }

    public Boolean getTvBoxed(int ordinal) {
        return tv;
    }

    public String getComment(int ordinal) {
        return comment;
    }

    public boolean isCommentEqual(int ordinal, String testValue) {
        if(testValue == null)
            return comment == null;
        return testValue.equals(comment);
    }

    public int getCommentOrdinal(int ordinal) {
        return commentOrdinal;
    }

    public boolean getOriginal(int ordinal) {
        if(original == null)
            return false;
        return original.booleanValue();
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return original;
    }

    public String getSubtype(int ordinal) {
        return subtype;
    }

    public boolean isSubtypeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return subtype == null;
        return testValue.equals(subtype);
    }

    public int getSubtypeOrdinal(int ordinal) {
        return subtypeOrdinal;
    }

    public boolean getTestTitle(int ordinal) {
        if(testTitle == null)
            return false;
        return testTitle.booleanValue();
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        return testTitle;
    }

    public int getMetadataReleaseDays(int ordinal) {
        if(metadataReleaseDays == null)
            return Integer.MIN_VALUE;
        return metadataReleaseDays.intValue();
    }

    public Integer getMetadataReleaseDaysBoxed(int ordinal) {
        return metadataReleaseDays;
    }

    public boolean getManualMetadataReleaseDaysUpdate(int ordinal) {
        if(manualMetadataReleaseDaysUpdate == null)
            return false;
        return manualMetadataReleaseDaysUpdate.booleanValue();
    }

    public Boolean getManualMetadataReleaseDaysUpdateBoxed(int ordinal) {
        return manualMetadataReleaseDaysUpdate;
    }

    public String getInternalTitle(int ordinal) {
        return internalTitle;
    }

    public boolean isInternalTitleEqual(int ordinal, String testValue) {
        if(testValue == null)
            return internalTitle == null;
        return testValue.equals(internalTitle);
    }

    public int getInternalTitleOrdinal(int ordinal) {
        return internalTitleOrdinal;
    }

    public String getInternalTitleBcpCode(int ordinal) {
        return internalTitleBcpCode;
    }

    public boolean isInternalTitleBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return internalTitleBcpCode == null;
        return testValue.equals(internalTitleBcpCode);
    }

    public int getInternalTitleBcpCodeOrdinal(int ordinal) {
        return internalTitleBcpCodeOrdinal;
    }

    public String getInternalTitlePart(int ordinal) {
        return internalTitlePart;
    }

    public boolean isInternalTitlePartEqual(int ordinal, String testValue) {
        if(testValue == null)
            return internalTitlePart == null;
        return testValue.equals(internalTitlePart);
    }

    public int getInternalTitlePartOrdinal(int ordinal) {
        return internalTitlePartOrdinal;
    }

    public String getInternalTitlePartBcpCode(int ordinal) {
        return internalTitlePartBcpCode;
    }

    public boolean isInternalTitlePartBcpCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return internalTitlePartBcpCode == null;
        return testValue.equals(internalTitlePartBcpCode);
    }

    public int getInternalTitlePartBcpCodeOrdinal(int ordinal) {
        return internalTitlePartBcpCodeOrdinal;
    }

    public String getSearchTitle(int ordinal) {
        return searchTitle;
    }

    public boolean isSearchTitleEqual(int ordinal, String testValue) {
        if(testValue == null)
            return searchTitle == null;
        return testValue.equals(searchTitle);
    }

    public int getSearchTitleOrdinal(int ordinal) {
        return searchTitleOrdinal;
    }

    public String getDirector(int ordinal) {
        return director;
    }

    public boolean isDirectorEqual(int ordinal, String testValue) {
        if(testValue == null)
            return director == null;
        return testValue.equals(director);
    }

    public int getDirectorOrdinal(int ordinal) {
        return directorOrdinal;
    }

    public String getCreator(int ordinal) {
        return creator;
    }

    public boolean isCreatorEqual(int ordinal, String testValue) {
        if(testValue == null)
            return creator == null;
        return testValue.equals(creator);
    }

    public int getCreatorOrdinal(int ordinal) {
        return creatorOrdinal;
    }

    public String getForceReason(int ordinal) {
        return forceReason;
    }

    public boolean isForceReasonEqual(int ordinal, String testValue) {
        if(testValue == null)
            return forceReason == null;
        return testValue.equals(forceReason);
    }

    public int getForceReasonOrdinal(int ordinal) {
        return forceReasonOrdinal;
    }

    public boolean getVisible(int ordinal) {
        if(visible == null)
            return false;
        return visible.booleanValue();
    }

    public Boolean getVisibleBoxed(int ordinal) {
        return visible;
    }

    public String getCreatedByTeam(int ordinal) {
        return createdByTeam;
    }

    public boolean isCreatedByTeamEqual(int ordinal, String testValue) {
        if(testValue == null)
            return createdByTeam == null;
        return testValue.equals(createdByTeam);
    }

    public int getCreatedByTeamOrdinal(int ordinal) {
        return createdByTeamOrdinal;
    }

    public String getUpdatedByTeam(int ordinal) {
        return updatedByTeam;
    }

    public boolean isUpdatedByTeamEqual(int ordinal, String testValue) {
        if(testValue == null)
            return updatedByTeam == null;
        return testValue.equals(updatedByTeam);
    }

    public int getUpdatedByTeamOrdinal(int ordinal) {
        return updatedByTeamOrdinal;
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

    public MovieTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (MovieTypeAPI) typeAPI;
    }

}