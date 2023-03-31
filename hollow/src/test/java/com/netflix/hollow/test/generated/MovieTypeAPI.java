package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieTypeAPI extends HollowObjectTypeAPI {

    private final MovieDelegateLookupImpl delegateLookupImpl;

    public MovieTypeAPI(MovieAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "id",
            "title",
            "year"
        });
        this.delegateLookupImpl = new MovieDelegateLookupImpl(this);
    }

    public long getId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Movie", ordinal, "id");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Movie", ordinal, "id");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTitleOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "title");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getTitleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getYear(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("Movie", ordinal, "year");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getYearBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("Movie", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public MovieDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public MovieAPI getAPI() {
        return (MovieAPI) api;
    }

}