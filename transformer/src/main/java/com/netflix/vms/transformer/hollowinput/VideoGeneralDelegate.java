package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoGeneralDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getAliasesOrdinal(int ordinal);

    public int getVideoTypeOrdinal(int ordinal);

    public long getRuntime(int ordinal);

    public Long getRuntimeBoxed(int ordinal);

    public int getSupplementalSubTypeOrdinal(int ordinal);

    public boolean getTestTitle(int ordinal);

    public Boolean getTestTitleBoxed(int ordinal);

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal);

    public int getTitleTypesOrdinal(int ordinal);

    public int getOriginCountryCodeOrdinal(int ordinal);

    public int getOriginalTitleOrdinal(int ordinal);

    public int getCountryOfOriginNameLocaleOrdinal(int ordinal);

    public int getInternalTitleOrdinal(int ordinal);

    public int getEpisodeTypesOrdinal(int ordinal);

    public VideoGeneralTypeAPI getTypeAPI();

}