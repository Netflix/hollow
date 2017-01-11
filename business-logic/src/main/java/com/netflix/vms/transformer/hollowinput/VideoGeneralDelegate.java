package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface VideoGeneralDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public boolean getTv(int ordinal);

    public Boolean getTvBoxed(int ordinal);

    public int getAliasesOrdinal(int ordinal);

    public int getVideoTypeOrdinal(int ordinal);

    public long getRuntime(int ordinal);

    public Long getRuntimeBoxed(int ordinal);

    public int getSupplementalSubTypeOrdinal(int ordinal);

    public long getFirstReleaseYear(int ordinal);

    public Long getFirstReleaseYearBoxed(int ordinal);

    public boolean getTestTitle(int ordinal);

    public Boolean getTestTitleBoxed(int ordinal);

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal);

    public int getMetadataReleaseDays(int ordinal);

    public Integer getMetadataReleaseDaysBoxed(int ordinal);

    public int getOriginCountryCodeOrdinal(int ordinal);

    public int getOriginalTitleOrdinal(int ordinal);

    public int getTestTitleTypesOrdinal(int ordinal);

    public int getOriginalTitleBcpCodeOrdinal(int ordinal);

    public int getInternalTitleOrdinal(int ordinal);

    public int getEpisodeTypesOrdinal(int ordinal);

    public int getRegulatoryAdvisoriesOrdinal(int ordinal);

    public VideoGeneralTypeAPI getTypeAPI();

}