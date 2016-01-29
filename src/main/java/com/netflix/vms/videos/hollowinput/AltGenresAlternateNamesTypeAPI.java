package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AltGenresAlternateNamesTypeAPI extends HollowObjectTypeAPI {

    private final AltGenresAlternateNamesDelegateLookupImpl delegateLookupImpl;

    AltGenresAlternateNamesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts",
            "typeId",
            "type"
        });
        this.delegateLookupImpl = new AltGenresAlternateNamesDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenresAlternateNames", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public AltGenresAlternateNamesMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getAltGenresAlternateNamesMapOfTranslatedTextsTypeAPI();
    }

    public long getTypeId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("AltGenresAlternateNames", ordinal, "typeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("AltGenresAlternateNames", ordinal, "typeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenresAlternateNames", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public AltGenresAlternateNamesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}