package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public int getTypeOrdinal(int ordinal);

    public String getOriginalLanguageBcpCode(int ordinal);

    public boolean isOriginalLanguageBcpCodeEqual(int ordinal, String testValue);

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal);

    public String getOriginalTitle(int ordinal);

    public boolean isOriginalTitleEqual(int ordinal, String testValue);

    public int getOriginalTitleOrdinal(int ordinal);

    public String getOriginalTitleBcpCode(int ordinal);

    public boolean isOriginalTitleBcpCodeEqual(int ordinal, String testValue);

    public int getOriginalTitleBcpCodeOrdinal(int ordinal);

    public String getOriginalTitleConcat(int ordinal);

    public boolean isOriginalTitleConcatEqual(int ordinal, String testValue);

    public int getOriginalTitleConcatOrdinal(int ordinal);

    public String getOriginalTitleConcatBcpCode(int ordinal);

    public boolean isOriginalTitleConcatBcpCodeEqual(int ordinal, String testValue);

    public int getOriginalTitleConcatBcpCodeOrdinal(int ordinal);

    public String getCountryOfOrigin(int ordinal);

    public boolean isCountryOfOriginEqual(int ordinal, String testValue);

    public int getCountryOfOriginOrdinal(int ordinal);

    public int getRunLenth(int ordinal);

    public Integer getRunLenthBoxed(int ordinal);

    public boolean getAvailableInPlastic(int ordinal);

    public Boolean getAvailableInPlasticBoxed(int ordinal);

    public int getFirstReleaseYear(int ordinal);

    public Integer getFirstReleaseYearBoxed(int ordinal);

    public boolean getActive(int ordinal);

    public Boolean getActiveBoxed(int ordinal);

    public boolean getTv(int ordinal);

    public Boolean getTvBoxed(int ordinal);

    public String getComment(int ordinal);

    public boolean isCommentEqual(int ordinal, String testValue);

    public int getCommentOrdinal(int ordinal);

    public boolean getOriginal(int ordinal);

    public Boolean getOriginalBoxed(int ordinal);

    public String getSubtype(int ordinal);

    public boolean isSubtypeEqual(int ordinal, String testValue);

    public int getSubtypeOrdinal(int ordinal);

    public boolean getTestTitle(int ordinal);

    public Boolean getTestTitleBoxed(int ordinal);

    public int getMetadataReleaseDays(int ordinal);

    public Integer getMetadataReleaseDaysBoxed(int ordinal);

    public boolean getManualMetadataReleaseDaysUpdate(int ordinal);

    public Boolean getManualMetadataReleaseDaysUpdateBoxed(int ordinal);

    public String getInternalTitle(int ordinal);

    public boolean isInternalTitleEqual(int ordinal, String testValue);

    public int getInternalTitleOrdinal(int ordinal);

    public String getInternalTitleBcpCode(int ordinal);

    public boolean isInternalTitleBcpCodeEqual(int ordinal, String testValue);

    public int getInternalTitleBcpCodeOrdinal(int ordinal);

    public String getInternalTitlePart(int ordinal);

    public boolean isInternalTitlePartEqual(int ordinal, String testValue);

    public int getInternalTitlePartOrdinal(int ordinal);

    public String getInternalTitlePartBcpCode(int ordinal);

    public boolean isInternalTitlePartBcpCodeEqual(int ordinal, String testValue);

    public int getInternalTitlePartBcpCodeOrdinal(int ordinal);

    public String getSearchTitle(int ordinal);

    public boolean isSearchTitleEqual(int ordinal, String testValue);

    public int getSearchTitleOrdinal(int ordinal);

    public String getDirector(int ordinal);

    public boolean isDirectorEqual(int ordinal, String testValue);

    public int getDirectorOrdinal(int ordinal);

    public String getCreator(int ordinal);

    public boolean isCreatorEqual(int ordinal, String testValue);

    public int getCreatorOrdinal(int ordinal);

    public String getForceReason(int ordinal);

    public boolean isForceReasonEqual(int ordinal, String testValue);

    public int getForceReasonOrdinal(int ordinal);

    public boolean getVisible(int ordinal);

    public Boolean getVisibleBoxed(int ordinal);

    public boolean getInteractive(int ordinal);

    public Boolean getInteractiveBoxed(int ordinal);

    public String getInteractiveType(int ordinal);

    public boolean isInteractiveTypeEqual(int ordinal, String testValue);

    public int getInteractiveTypeOrdinal(int ordinal);

    public int getDisplayRunLength(int ordinal);

    public Integer getDisplayRunLengthBoxed(int ordinal);

    public int getInteractiveShortestRunLength(int ordinal);

    public Integer getInteractiveShortestRunLengthBoxed(int ordinal);

    public String getCreatedByTeam(int ordinal);

    public boolean isCreatedByTeamEqual(int ordinal, String testValue);

    public int getCreatedByTeamOrdinal(int ordinal);

    public String getUpdatedByTeam(int ordinal);

    public boolean isUpdatedByTeamEqual(int ordinal, String testValue);

    public int getUpdatedByTeamOrdinal(int ordinal);

    public long getDateCreated(int ordinal);

    public Long getDateCreatedBoxed(int ordinal);

    public int getDateCreatedOrdinal(int ordinal);

    public long getLastUpdated(int ordinal);

    public Long getLastUpdatedBoxed(int ordinal);

    public int getLastUpdatedOrdinal(int ordinal);

    public String getCreatedBy(int ordinal);

    public boolean isCreatedByEqual(int ordinal, String testValue);

    public int getCreatedByOrdinal(int ordinal);

    public String getUpdatedBy(int ordinal);

    public boolean isUpdatedByEqual(int ordinal, String testValue);

    public int getUpdatedByOrdinal(int ordinal);

    public MovieTypeAPI getTypeAPI();

}