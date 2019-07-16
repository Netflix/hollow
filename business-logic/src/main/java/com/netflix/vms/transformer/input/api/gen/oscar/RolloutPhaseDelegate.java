package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RolloutPhaseDelegate extends HollowObjectDelegate {

    public long getRolloutPhaseId(int ordinal);

    public Long getRolloutPhaseIdBoxed(int ordinal);

    public String getPhaseName(int ordinal);

    public boolean isPhaseNameEqual(int ordinal, String testValue);

    public int getPhaseNameOrdinal(int ordinal);

    public long getStartDate(int ordinal);

    public Long getStartDateBoxed(int ordinal);

    public int getStartDateOrdinal(int ordinal);

    public long getEndDate(int ordinal);

    public Long getEndDateBoxed(int ordinal);

    public int getEndDateOrdinal(int ordinal);

    public String getWindowType(int ordinal);

    public boolean isWindowTypeEqual(int ordinal, String testValue);

    public int getWindowTypeOrdinal(int ordinal);

    public boolean getShowCoreMetadata(int ordinal);

    public Boolean getShowCoreMetadataBoxed(int ordinal);

    public boolean getOnHold(int ordinal);

    public Boolean getOnHoldBoxed(int ordinal);

    public long getSeasonMovieId(int ordinal);

    public Long getSeasonMovieIdBoxed(int ordinal);

    public int getSeasonMovieIdOrdinal(int ordinal);

    public String getPhaseType(int ordinal);

    public boolean isPhaseTypeEqual(int ordinal, String testValue);

    public int getPhaseTypeOrdinal(int ordinal);

    public int getTrailersOrdinal(int ordinal);

    public int getCastMembersOrdinal(int ordinal);

    public int getMetadataElementsOrdinal(int ordinal);

    public int getPhaseArtworksOrdinal(int ordinal);

    public int getRequiredImageTypesOrdinal(int ordinal);

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

    public RolloutPhaseTypeAPI getTypeAPI();

}