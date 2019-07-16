package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface MovieReleaseHistoryDelegate extends HollowObjectDelegate {

    public long getMovieId(int ordinal);

    public Long getMovieIdBoxed(int ordinal);

    public int getMovieIdOrdinal(int ordinal);

    public String getCountryCode(int ordinal);

    public boolean isCountryCodeEqual(int ordinal, String testValue);

    public int getCountryCodeOrdinal(int ordinal);

    public String getType(int ordinal);

    public boolean isTypeEqual(int ordinal, String testValue);

    public int getTypeOrdinal(int ordinal);

    public int getYear(int ordinal);

    public Integer getYearBoxed(int ordinal);

    public int getMonth(int ordinal);

    public Integer getMonthBoxed(int ordinal);

    public int getDay(int ordinal);

    public Integer getDayBoxed(int ordinal);

    public String getDistributorName(int ordinal);

    public boolean isDistributorNameEqual(int ordinal, String testValue);

    public int getDistributorNameOrdinal(int ordinal);

    public String getDistributorBcpCode(int ordinal);

    public boolean isDistributorBcpCodeEqual(int ordinal, String testValue);

    public int getDistributorBcpCodeOrdinal(int ordinal);

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

    public MovieReleaseHistoryTypeAPI getTypeAPI();

}