package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

@SuppressWarnings("all")
public class MapOfTranslatedTextTypeAPI extends HollowMapTypeAPI {

    private final HollowMapLookupDelegate delegateLookupImpl;

    public MapOfTranslatedTextTypeAPI(VMSHollowInputAPI api, HollowMapTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowMapLookupDelegate(this);
    }

    public MapKeyTypeAPI getKeyAPI() {
        return getAPI().getMapKeyTypeAPI();
    }

    public TranslatedTextValueTypeAPI getValueAPI() {
        return getAPI().getTranslatedTextValueTypeAPI();
    }

    public HollowMapLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI)api;
    }

}