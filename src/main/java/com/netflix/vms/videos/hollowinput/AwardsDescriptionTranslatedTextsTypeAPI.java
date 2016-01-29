package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AwardsDescriptionTranslatedTextsTypeAPI extends HollowObjectTypeAPI {

    private final AwardsDescriptionTranslatedTextsDelegateLookupImpl delegateLookupImpl;

    AwardsDescriptionTranslatedTextsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
        });
        this.delegateLookupImpl = new AwardsDescriptionTranslatedTextsDelegateLookupImpl(this);
    }

    public AwardsDescriptionTranslatedTextsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}