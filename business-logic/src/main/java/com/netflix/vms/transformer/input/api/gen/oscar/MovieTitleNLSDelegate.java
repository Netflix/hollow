package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieTitleNLSDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getType(int ordinal);

    public boolean isTypeEqual(int ordinal, String testValue);

    public int getTypeOrdinal(int ordinal);

    public String getTitleText(int ordinal);

    public boolean isTitleTextEqual(int ordinal, String testValue);

    public int getTitleTextOrdinal(int ordinal);

    public String getMerchBcpCode(int ordinal);

    public boolean isMerchBcpCodeEqual(int ordinal, String testValue);

    public int getMerchBcpCodeOrdinal(int ordinal);

    public String getTitleBcpCode(int ordinal);

    public boolean isTitleBcpCodeEqual(int ordinal, String testValue);

    public int getTitleBcpCodeOrdinal(int ordinal);

    public String getSourceType(int ordinal);

    public boolean isSourceTypeEqual(int ordinal, String testValue);

    public int getSourceTypeOrdinal(int ordinal);

    public boolean getIsOriginalTitle(int ordinal);

    public Boolean getIsOriginalTitleBoxed(int ordinal);

    public int getIsOriginalTitleOrdinal(int ordinal);

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

    public MovieTitleNLSTypeAPI getTypeAPI();

}