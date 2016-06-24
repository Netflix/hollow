package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoDateWindowTypeAPI extends HollowObjectTypeAPI {

    private final VideoDateWindowDelegateLookupImpl delegateLookupImpl;

    VideoDateWindowTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCode",
            "releaseDates"
        });
        this.delegateLookupImpl = new VideoDateWindowDelegateLookupImpl(this);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDateWindow", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getReleaseDatesOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDateWindow", ordinal, "releaseDates");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public ListOfReleaseDatesTypeAPI getReleaseDatesTypeAPI() {
        return getAPI().getListOfReleaseDatesTypeAPI();
    }

    public VideoDateWindowDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}