package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TopNAttributeTypeAPI extends HollowObjectTypeAPI {

    private final TopNAttributeDelegateLookupImpl delegateLookupImpl;

    public TopNAttributeTypeAPI(TopNAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "country",
            "countryViewHoursDaily",
            "videoViewHoursDaily"
        });
        this.delegateLookupImpl = new TopNAttributeDelegateLookupImpl(this);
    }

    public int getCountryOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TopNAttribute", ordinal, "country");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getCountryViewHoursDaily(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("TopNAttribute", ordinal, "countryViewHoursDaily");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getCountryViewHoursDailyBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("TopNAttribute", ordinal, "countryViewHoursDaily");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getVideoViewHoursDaily(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("TopNAttribute", ordinal, "videoViewHoursDaily");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getVideoViewHoursDailyBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("TopNAttribute", ordinal, "videoViewHoursDaily");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public TopNAttributeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public TopNAPI getAPI() {
        return (TopNAPI) api;
    }

}