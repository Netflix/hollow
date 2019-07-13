package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class TranslatedTextValueTypeAPI extends HollowObjectTypeAPI {

    private final TranslatedTextValueDelegateLookupImpl delegateLookupImpl;

    public TranslatedTextValueTypeAPI(LocalizedMetaDataAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new TranslatedTextValueDelegateLookupImpl(this);
    }

    public int getValueOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("TranslatedTextValue", ordinal, "value");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getValueTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public TranslatedTextValueDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public LocalizedMetaDataAPI getAPI() {
        return (LocalizedMetaDataAPI) api;
    }

}