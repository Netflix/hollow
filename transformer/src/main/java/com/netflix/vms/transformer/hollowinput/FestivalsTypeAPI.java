package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class FestivalsTypeAPI extends HollowObjectTypeAPI {

    private final FestivalsDelegateLookupImpl delegateLookupImpl;

    FestivalsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "festivalId",
            "copyright",
            "festivalName",
            "description",
            "shortName",
            "singularName"
        });
        this.delegateLookupImpl = new FestivalsDelegateLookupImpl(this);
    }

    public long getFestivalId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Festivals", ordinal, "festivalId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getFestivalIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Festivals", ordinal, "festivalId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCopyrightOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "copyright");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getCopyrightTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getFestivalNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "festivalName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public TranslatedTextTypeAPI getFestivalNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public TranslatedTextTypeAPI getDescriptionTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getShortNameOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "shortName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public TranslatedTextTypeAPI getShortNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public int getSingularNameOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Festivals", ordinal, "singularName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public TranslatedTextTypeAPI getSingularNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public FestivalsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}