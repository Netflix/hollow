package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class Stories_SynopsesHooksTypeAPI extends HollowObjectTypeAPI {

    private final Stories_SynopsesHooksDelegateLookupImpl delegateLookupImpl;

    Stories_SynopsesHooksTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts",
            "rank",
            "type"
        });
        this.delegateLookupImpl = new Stories_SynopsesHooksDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Stories_SynopsesHooks", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public Stories_SynopsesHooksMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getStories_SynopsesHooksMapOfTranslatedTextsTypeAPI();
    }

    public int getRankOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Stories_SynopsesHooks", ordinal, "rank");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getRankTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Stories_SynopsesHooks", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public Stories_SynopsesHooksDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}