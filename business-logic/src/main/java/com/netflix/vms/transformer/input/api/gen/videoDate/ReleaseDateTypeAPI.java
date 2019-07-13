package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class ReleaseDateTypeAPI extends HollowObjectTypeAPI {

    private final ReleaseDateDelegateLookupImpl delegateLookupImpl;

    public ReleaseDateTypeAPI(VideoDateAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "releaseDateType",
            "distributorName",
            "month",
            "year",
            "day",
            "bcp47code"
        });
        this.delegateLookupImpl = new ReleaseDateDelegateLookupImpl(this);
    }

    public int getReleaseDateTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("ReleaseDate", ordinal, "releaseDateType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getReleaseDateTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDistributorNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ReleaseDate", ordinal, "distributorName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getDistributorNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getMonth(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("ReleaseDate", ordinal, "month");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getMonthBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("ReleaseDate", ordinal, "month");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getYear(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("ReleaseDate", ordinal, "year");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getYearBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("ReleaseDate", ordinal, "year");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getDay(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleInt("ReleaseDate", ordinal, "day");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
    }

    public Integer getDayBoxed(int ordinal) {
        int i;
        if(fieldIndex[4] == -1) {
            i = missingDataHandler().handleInt("ReleaseDate", ordinal, "day");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getBcp47codeOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("ReleaseDate", ordinal, "bcp47code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getBcp47codeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public ReleaseDateDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VideoDateAPI getAPI() {
        return (VideoDateAPI) api;
    }

}