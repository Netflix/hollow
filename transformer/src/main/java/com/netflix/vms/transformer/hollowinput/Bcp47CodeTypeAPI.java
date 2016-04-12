package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class Bcp47CodeTypeAPI extends HollowObjectTypeAPI {

    private final Bcp47CodeDelegateLookupImpl delegateLookupImpl;

    Bcp47CodeTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "languageId",
            "iso6392Code",
            "bcp47Code",
            "iso6391Code",
            "iso6393Code"
        });
        this.delegateLookupImpl = new Bcp47CodeDelegateLookupImpl(this);
    }

    public long getLanguageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Bcp47Code", ordinal, "languageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getLanguageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Bcp47Code", ordinal, "languageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getIso6392CodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Bcp47Code", ordinal, "iso6392Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getIso6392CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getBcp47CodeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Bcp47Code", ordinal, "bcp47Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getBcp47CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getIso6391CodeOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Bcp47Code", ordinal, "iso6391Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public StringTypeAPI getIso6391CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getIso6393CodeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Bcp47Code", ordinal, "iso6393Code");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getIso6393CodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public Bcp47CodeDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}