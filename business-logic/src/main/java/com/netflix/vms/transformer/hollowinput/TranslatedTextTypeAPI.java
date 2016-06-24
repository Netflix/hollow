package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TranslatedTextTypeAPI extends HollowObjectTypeAPI {

    private final TranslatedTextDelegateLookupImpl delegateLookupImpl;

    TranslatedTextTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new TranslatedTextDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TranslatedText", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MapOfTranslatedTextTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMapOfTranslatedTextTypeAPI();
    }

    public TranslatedTextDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}