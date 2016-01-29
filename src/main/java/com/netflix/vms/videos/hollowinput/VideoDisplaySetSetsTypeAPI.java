package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoDisplaySetSetsTypeAPI extends HollowObjectTypeAPI {

    private final VideoDisplaySetSetsDelegateLookupImpl delegateLookupImpl;

    VideoDisplaySetSetsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "children",
            "countryCode",
            "setType"
        });
        this.delegateLookupImpl = new VideoDisplaySetSetsDelegateLookupImpl(this);
    }

    public int getChildrenOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDisplaySetSets", ordinal, "children");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoDisplaySetSetsArrayOfChildrenTypeAPI getChildrenTypeAPI() {
        return getAPI().getVideoDisplaySetSetsArrayOfChildrenTypeAPI();
    }

    public int getCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDisplaySetSets", ordinal, "countryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSetTypeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoDisplaySetSets", ordinal, "setType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getSetTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public VideoDisplaySetSetsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}