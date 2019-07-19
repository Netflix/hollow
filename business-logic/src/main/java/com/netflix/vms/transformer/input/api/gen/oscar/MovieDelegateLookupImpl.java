package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieDelegate {

    private final MovieTypeAPI typeAPI;

    public MovieDelegateLookupImpl(MovieTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getMovieIdTypeAPI().getValue(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        ordinal = typeAPI.getMovieIdOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieIdTypeAPI().getValueBoxed(ordinal);
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public int getTypeOrdinal(int ordinal) {
        return typeAPI.getTypeOrdinal(ordinal);
    }

    public String getOriginalLanguageBcpCode(int ordinal) {
        ordinal = typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalLanguageBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        return typeAPI.getOriginalLanguageBcpCodeOrdinal(ordinal);
    }

    public String getOriginalTitle(int ordinal) {
        ordinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalTitleEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalTitleOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleOrdinal(ordinal);
    }

    public String getOriginalTitleBcpCode(int ordinal) {
        ordinal = typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalTitleBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalTitleBcpCodeOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleBcpCodeOrdinal(ordinal);
    }

    public String getOriginalTitleConcat(int ordinal) {
        ordinal = typeAPI.getOriginalTitleConcatOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalTitleConcatEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalTitleConcatOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalTitleConcatOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleConcatOrdinal(ordinal);
    }

    public String getOriginalTitleConcatBcpCode(int ordinal) {
        ordinal = typeAPI.getOriginalTitleConcatBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isOriginalTitleConcatBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getOriginalTitleConcatBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getOriginalTitleConcatBcpCodeOrdinal(int ordinal) {
        return typeAPI.getOriginalTitleConcatBcpCodeOrdinal(ordinal);
    }

    public String getCountryOfOrigin(int ordinal) {
        ordinal = typeAPI.getCountryOfOriginOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getCountryStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryOfOriginEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryOfOriginOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getCountryStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryOfOriginOrdinal(int ordinal) {
        return typeAPI.getCountryOfOriginOrdinal(ordinal);
    }

    public int getRunLenth(int ordinal) {
        return typeAPI.getRunLenth(ordinal);
    }

    public Integer getRunLenthBoxed(int ordinal) {
        return typeAPI.getRunLenthBoxed(ordinal);
    }

    public boolean getAvailableInPlastic(int ordinal) {
        return typeAPI.getAvailableInPlastic(ordinal);
    }

    public Boolean getAvailableInPlasticBoxed(int ordinal) {
        return typeAPI.getAvailableInPlasticBoxed(ordinal);
    }

    public int getFirstReleaseYear(int ordinal) {
        return typeAPI.getFirstReleaseYear(ordinal);
    }

    public Integer getFirstReleaseYearBoxed(int ordinal) {
        return typeAPI.getFirstReleaseYearBoxed(ordinal);
    }

    public boolean getActive(int ordinal) {
        return typeAPI.getActive(ordinal);
    }

    public Boolean getActiveBoxed(int ordinal) {
        return typeAPI.getActiveBoxed(ordinal);
    }

    public boolean getTv(int ordinal) {
        return typeAPI.getTv(ordinal);
    }

    public Boolean getTvBoxed(int ordinal) {
        return typeAPI.getTvBoxed(ordinal);
    }

    public String getComment(int ordinal) {
        ordinal = typeAPI.getCommentOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCommentEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCommentOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCommentOrdinal(int ordinal) {
        return typeAPI.getCommentOrdinal(ordinal);
    }

    public boolean getOriginal(int ordinal) {
        return typeAPI.getOriginal(ordinal);
    }

    public Boolean getOriginalBoxed(int ordinal) {
        return typeAPI.getOriginalBoxed(ordinal);
    }

    public String getSubtype(int ordinal) {
        ordinal = typeAPI.getSubtypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getSupplementalSubtypeTypeAPI().getValue(ordinal);
    }

    public boolean isSubtypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSubtypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getSupplementalSubtypeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getSubtypeOrdinal(int ordinal) {
        return typeAPI.getSubtypeOrdinal(ordinal);
    }

    public boolean getTestTitle(int ordinal) {
        return typeAPI.getTestTitle(ordinal);
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        return typeAPI.getTestTitleBoxed(ordinal);
    }

    public int getMetadataReleaseDays(int ordinal) {
        return typeAPI.getMetadataReleaseDays(ordinal);
    }

    public Integer getMetadataReleaseDaysBoxed(int ordinal) {
        return typeAPI.getMetadataReleaseDaysBoxed(ordinal);
    }

    public boolean getManualMetadataReleaseDaysUpdate(int ordinal) {
        return typeAPI.getManualMetadataReleaseDaysUpdate(ordinal);
    }

    public Boolean getManualMetadataReleaseDaysUpdateBoxed(int ordinal) {
        return typeAPI.getManualMetadataReleaseDaysUpdateBoxed(ordinal);
    }

    public String getInternalTitle(int ordinal) {
        ordinal = typeAPI.getInternalTitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isInternalTitleEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInternalTitleOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInternalTitleOrdinal(int ordinal) {
        return typeAPI.getInternalTitleOrdinal(ordinal);
    }

    public String getInternalTitleBcpCode(int ordinal) {
        ordinal = typeAPI.getInternalTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isInternalTitleBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInternalTitleBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInternalTitleBcpCodeOrdinal(int ordinal) {
        return typeAPI.getInternalTitleBcpCodeOrdinal(ordinal);
    }

    public String getInternalTitlePart(int ordinal) {
        ordinal = typeAPI.getInternalTitlePartOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isInternalTitlePartEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInternalTitlePartOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInternalTitlePartOrdinal(int ordinal) {
        return typeAPI.getInternalTitlePartOrdinal(ordinal);
    }

    public String getInternalTitlePartBcpCode(int ordinal) {
        ordinal = typeAPI.getInternalTitlePartBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getBcpCodeTypeAPI().getValue(ordinal);
    }

    public boolean isInternalTitlePartBcpCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInternalTitlePartBcpCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getBcpCodeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInternalTitlePartBcpCodeOrdinal(int ordinal) {
        return typeAPI.getInternalTitlePartBcpCodeOrdinal(ordinal);
    }

    public String getSearchTitle(int ordinal) {
        ordinal = typeAPI.getSearchTitleOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getMovieTitleStringTypeAPI().getValue(ordinal);
    }

    public boolean isSearchTitleEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getSearchTitleOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getMovieTitleStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getSearchTitleOrdinal(int ordinal) {
        return typeAPI.getSearchTitleOrdinal(ordinal);
    }

    public String getDirector(int ordinal) {
        ordinal = typeAPI.getDirectorOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getPersonNameTypeAPI().getValue(ordinal);
    }

    public boolean isDirectorEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getDirectorOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getPersonNameTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getDirectorOrdinal(int ordinal) {
        return typeAPI.getDirectorOrdinal(ordinal);
    }

    public String getCreator(int ordinal) {
        ordinal = typeAPI.getCreatorOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getPersonNameTypeAPI().getValue(ordinal);
    }

    public boolean isCreatorEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCreatorOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getPersonNameTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCreatorOrdinal(int ordinal) {
        return typeAPI.getCreatorOrdinal(ordinal);
    }

    public String getForceReason(int ordinal) {
        ordinal = typeAPI.getForceReasonOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getForceReasonTypeAPI().getValue(ordinal);
    }

    public boolean isForceReasonEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getForceReasonOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getForceReasonTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getForceReasonOrdinal(int ordinal) {
        return typeAPI.getForceReasonOrdinal(ordinal);
    }

    public boolean getVisible(int ordinal) {
        return typeAPI.getVisible(ordinal);
    }

    public Boolean getVisibleBoxed(int ordinal) {
        return typeAPI.getVisibleBoxed(ordinal);
    }

    public boolean getInteractive(int ordinal) {
        return typeAPI.getInteractive(ordinal);
    }

    public Boolean getInteractiveBoxed(int ordinal) {
        return typeAPI.getInteractiveBoxed(ordinal);
    }

    public String getInteractiveType(int ordinal) {
        ordinal = typeAPI.getInteractiveTypeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getInteractiveTypeTypeAPI().getValue(ordinal);
    }

    public boolean isInteractiveTypeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getInteractiveTypeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getInteractiveTypeTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getInteractiveTypeOrdinal(int ordinal) {
        return typeAPI.getInteractiveTypeOrdinal(ordinal);
    }

    public int getDisplayRunLength(int ordinal) {
        return typeAPI.getDisplayRunLength(ordinal);
    }

    public Integer getDisplayRunLengthBoxed(int ordinal) {
        return typeAPI.getDisplayRunLengthBoxed(ordinal);
    }

    public int getInteractiveShortestRunLength(int ordinal) {
        return typeAPI.getInteractiveShortestRunLength(ordinal);
    }

    public Integer getInteractiveShortestRunLengthBoxed(int ordinal) {
        return typeAPI.getInteractiveShortestRunLengthBoxed(ordinal);
    }

    public String getCreatedByTeam(int ordinal) {
        ordinal = typeAPI.getCreatedByTeamOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCreatedByTeamEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCreatedByTeamOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCreatedByTeamOrdinal(int ordinal) {
        return typeAPI.getCreatedByTeamOrdinal(ordinal);
    }

    public String getUpdatedByTeam(int ordinal) {
        ordinal = typeAPI.getUpdatedByTeamOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isUpdatedByTeamEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getUpdatedByTeamOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getUpdatedByTeamOrdinal(int ordinal) {
        return typeAPI.getUpdatedByTeamOrdinal(ordinal);
    }

    public long getDateCreated(int ordinal) {
        ordinal = typeAPI.getDateCreatedOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getDateTypeAPI().getValue(ordinal);
    }

    public Long getDateCreatedBoxed(int ordinal) {
        ordinal = typeAPI.getDateCreatedOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValueBoxed(ordinal);
    }

    public int getDateCreatedOrdinal(int ordinal) {
        return typeAPI.getDateCreatedOrdinal(ordinal);
    }

    public long getLastUpdated(int ordinal) {
        ordinal = typeAPI.getLastUpdatedOrdinal(ordinal);
        return ordinal == -1 ? Long.MIN_VALUE : typeAPI.getAPI().getDateTypeAPI().getValue(ordinal);
    }

    public Long getLastUpdatedBoxed(int ordinal) {
        ordinal = typeAPI.getLastUpdatedOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getDateTypeAPI().getValueBoxed(ordinal);
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        return typeAPI.getLastUpdatedOrdinal(ordinal);
    }

    public String getCreatedBy(int ordinal) {
        ordinal = typeAPI.getCreatedByOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCreatedByEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCreatedByOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCreatedByOrdinal(int ordinal) {
        return typeAPI.getCreatedByOrdinal(ordinal);
    }

    public String getUpdatedBy(int ordinal) {
        ordinal = typeAPI.getUpdatedByOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isUpdatedByEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getUpdatedByOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getUpdatedByOrdinal(int ordinal) {
        return typeAPI.getUpdatedByOrdinal(ordinal);
    }

    public MovieTypeAPI getTypeAPI() {
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