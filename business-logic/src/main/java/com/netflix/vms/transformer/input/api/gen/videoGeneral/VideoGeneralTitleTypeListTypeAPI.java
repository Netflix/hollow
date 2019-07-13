package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;

@SuppressWarnings("all")
public class VideoGeneralTitleTypeListTypeAPI extends HollowListTypeAPI {

    private final HollowListLookupDelegate delegateLookupImpl;

    public VideoGeneralTitleTypeListTypeAPI(VideoGeneralAPI api, HollowListTypeDataAccess dataAccess) {
        super(api, dataAccess);
        this.delegateLookupImpl = new HollowListLookupDelegate(this);
    }

    public VideoGeneralTitleTypeTypeAPI getElementAPI() {
        return getAPI().getVideoGeneralTitleTypeTypeAPI();
    }

    public HollowListLookupDelegate getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    public VideoGeneralAPI getAPI() {
        return (VideoGeneralAPI)api;
    }

}