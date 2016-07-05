package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AltGenresAlternateNamesTypeAPI extends HollowObjectTypeAPI {

    private final AltGenresAlternateNamesDelegateLookupImpl delegateLookupImpl;

    AltGenresAlternateNamesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "typeId",
            "type",
            "translatedTexts"
        });
        this.delegateLookupImpl = new AltGenresAlternateNamesDelegateLookupImpl(this);
    }

    public long getTypeId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("AltGenresAlternateNames", ordinal, "typeId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getTypeIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("AltGenresAlternateNames", ordinal, "typeId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenresAlternateNames", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("AltGenresAlternateNames", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MapOfTranslatedTextTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMapOfTranslatedTextTypeAPI();
    }

    public AltGenresAlternateNamesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}