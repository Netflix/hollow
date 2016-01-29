package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class FestivalsTypeAPI extends HollowObjectTypeAPI {

    private final FestivalsDelegateLookupImpl delegateLookupImpl;

    FestivalsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "copyright",
            "festivalId",
            "festivalName",
            "description",
            "shortName",
            "singularName"
        });
        this.delegateLookupImpl = new FestivalsDelegateLookupImpl(this);
    }

    public int getCopyrightOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "copyright");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public FestivalsCopyrightTypeAPI getCopyrightTypeAPI() {
        return getAPI().getFestivalsCopyrightTypeAPI();
    }

    public long getFestivalId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("Festivals", ordinal, "festivalId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getFestivalIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Festivals", ordinal, "festivalId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getFestivalNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "festivalName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public FestivalsFestivalNameTypeAPI getFestivalNameTypeAPI() {
        return getAPI().getFestivalsFestivalNameTypeAPI();
    }

    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public FestivalsDescriptionTypeAPI getDescriptionTypeAPI() {
        return getAPI().getFestivalsDescriptionTypeAPI();
    }

    public int getShortNameOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "shortName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public FestivalsShortNameTypeAPI getShortNameTypeAPI() {
        return getAPI().getFestivalsShortNameTypeAPI();
    }

    public int getSingularNameOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "singularName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public FestivalsSingularNameTypeAPI getSingularNameTypeAPI() {
        return getAPI().getFestivalsSingularNameTypeAPI();
    }

    public FestivalsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}