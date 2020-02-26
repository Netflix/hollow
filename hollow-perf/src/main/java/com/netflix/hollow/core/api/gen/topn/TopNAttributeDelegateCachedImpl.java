package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TopNAttributeDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, TopNAttributeDelegate {

    private final String country;
    private final int countryOrdinal;
    private final Long countryViewHoursDaily;
    private final Long videoViewHoursDaily;
    private TopNAttributeTypeAPI typeAPI;

    public TopNAttributeDelegateCachedImpl(TopNAttributeTypeAPI typeAPI, int ordinal) {
        this.countryOrdinal = typeAPI.getCountryOrdinal(ordinal);
        int countryTempOrdinal = countryOrdinal;
        this.country = countryTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryTempOrdinal);
        this.countryViewHoursDaily = typeAPI.getCountryViewHoursDailyBoxed(ordinal);
        this.videoViewHoursDaily = typeAPI.getVideoViewHoursDailyBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public String getCountry(int ordinal) {
        return country;
    }

    public boolean isCountryEqual(int ordinal, String testValue) {
        if(testValue == null)
            return country == null;
        return testValue.equals(country);
    }

    public int getCountryOrdinal(int ordinal) {
        return countryOrdinal;
    }

    public long getCountryViewHoursDaily(int ordinal) {
        if(countryViewHoursDaily == null)
            return Long.MIN_VALUE;
        return countryViewHoursDaily.longValue();
    }

    public Long getCountryViewHoursDailyBoxed(int ordinal) {
        return countryViewHoursDaily;
    }

    public long getVideoViewHoursDaily(int ordinal) {
        if(videoViewHoursDaily == null)
            return Long.MIN_VALUE;
        return videoViewHoursDaily.longValue();
    }

    public Long getVideoViewHoursDailyBoxed(int ordinal) {
        return videoViewHoursDaily;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public TopNAttributeTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (TopNAttributeTypeAPI) typeAPI;
    }

}