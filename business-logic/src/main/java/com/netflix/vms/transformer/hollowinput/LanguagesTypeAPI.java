package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class LanguagesTypeAPI extends HollowObjectTypeAPI {

    private final LanguagesDelegateLookupImpl delegateLookupImpl;

    LanguagesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "languageId",
            "name"
        });
        this.delegateLookupImpl = new LanguagesDelegateLookupImpl(this);
    }

    public long getLanguageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Languages", ordinal, "languageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getLanguageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Languages", ordinal, "languageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getNameOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Languages", ordinal, "name");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public TranslatedTextTypeAPI getNameTypeAPI() {
        return getAPI().getTranslatedTextTypeAPI();
    }

    public LanguagesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}