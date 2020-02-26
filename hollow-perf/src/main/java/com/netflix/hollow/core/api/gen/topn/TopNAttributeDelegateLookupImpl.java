package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TopNAttributeDelegateLookupImpl extends HollowObjectAbstractDelegate implements TopNAttributeDelegate {

    private final TopNAttributeTypeAPI typeAPI;

    public TopNAttributeDelegateLookupImpl(TopNAttributeTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public String getCountry(int ordinal) {
        ordinal = typeAPI.getCountryOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryOrdinal(int ordinal) {
        return typeAPI.getCountryOrdinal(ordinal);
    }

    public long getCountryViewHoursDaily(int ordinal) {
        return typeAPI.getCountryViewHoursDaily(ordinal);
    }

    public Long getCountryViewHoursDailyBoxed(int ordinal) {
        return typeAPI.getCountryViewHoursDailyBoxed(ordinal);
    }

    public long getVideoViewHoursDaily(int ordinal) {
        return typeAPI.getVideoViewHoursDaily(ordinal);
    }

    public Long getVideoViewHoursDailyBoxed(int ordinal) {
        return typeAPI.getVideoViewHoursDailyBoxed(ordinal);
    }

    public TopNAttributeTypeAPI getTypeAPI() {
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