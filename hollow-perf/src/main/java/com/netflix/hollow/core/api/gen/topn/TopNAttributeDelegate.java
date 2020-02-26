package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface TopNAttributeDelegate extends HollowObjectDelegate {

    public String getCountry(int ordinal);

    public boolean isCountryEqual(int ordinal, String testValue);

    public int getCountryOrdinal(int ordinal);

    public long getCountryViewHoursDaily(int ordinal);

    public Long getCountryViewHoursDailyBoxed(int ordinal);

    public long getVideoViewHoursDaily(int ordinal);

    public Long getVideoViewHoursDailyBoxed(int ordinal);

    public TopNAttributeTypeAPI getTypeAPI();

}