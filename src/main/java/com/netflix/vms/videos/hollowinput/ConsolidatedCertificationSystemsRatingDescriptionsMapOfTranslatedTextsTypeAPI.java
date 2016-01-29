package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowMapTypeAPI;

import com.netflix.hollow.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.objects.delegate.HollowMapLookupDelegate;

public class ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    ConsolidatedCertificationSystemsRatingDescriptionsMapOfTranslatedTextsTypeAPI(VMSHollowVideoInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public MapKeyTypeAPI getKeyAPI() {
        return getAPI().getMapKeyTypeAPI();
    }

    public ConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI getValueAPI() {
        return getAPI().getConsolidatedCertificationSystemsRatingDescriptionsTranslatedTextsTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI)api;
    }

}