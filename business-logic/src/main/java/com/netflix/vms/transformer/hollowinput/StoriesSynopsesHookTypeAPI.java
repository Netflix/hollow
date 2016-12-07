package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StoriesSynopsesHookTypeAPI extends HollowObjectTypeAPI {

    private final StoriesSynopsesHookDelegateLookupImpl delegateLookupImpl;

    StoriesSynopsesHookTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "type",
            "rank",
            "translatedTexts"
        });
        this.delegateLookupImpl = new StoriesSynopsesHookDelegateLookupImpl(this);
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("StoriesSynopsesHook", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getRankOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("StoriesSynopsesHook", ordinal, "rank");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getRankTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("StoriesSynopsesHook", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public MapOfTranslatedTextTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMapOfTranslatedTextTypeAPI();
    }

    public StoriesSynopsesHookDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}