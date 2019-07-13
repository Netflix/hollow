package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ReleaseDateDelegate extends HollowObjectDelegate {

    public String getReleaseDateType(int ordinal);

    public boolean isReleaseDateTypeEqual(int ordinal, String testValue);

    public int getReleaseDateTypeOrdinal(int ordinal);

    public String getDistributorName(int ordinal);

    public boolean isDistributorNameEqual(int ordinal, String testValue);

    public int getDistributorNameOrdinal(int ordinal);

    public int getMonth(int ordinal);

    public Integer getMonthBoxed(int ordinal);

    public int getYear(int ordinal);

    public Integer getYearBoxed(int ordinal);

    public int getDay(int ordinal);

    public Integer getDayBoxed(int ordinal);

    public String getBcp47code(int ordinal);

    public boolean isBcp47codeEqual(int ordinal, String testValue);

    public int getBcp47codeOrdinal(int ordinal);

    public ReleaseDateTypeAPI getTypeAPI();

}