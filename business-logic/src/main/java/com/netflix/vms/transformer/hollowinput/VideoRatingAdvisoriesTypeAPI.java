package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoRatingAdvisoriesTypeAPI extends HollowObjectTypeAPI {

    private final VideoRatingAdvisoriesDelegateLookupImpl delegateLookupImpl;

    public VideoRatingAdvisoriesTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ordered",
            "imageOnly",
            "ids"
        });
        this.delegateLookupImpl = new VideoRatingAdvisoriesDelegateLookupImpl(this);
    }

    public boolean getOrdered(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRatingAdvisories", ordinal, "ordered") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getOrderedBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRatingAdvisories", ordinal, "ordered");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getImageOnly(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRatingAdvisories", ordinal, "imageOnly") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getImageOnlyBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRatingAdvisories", ordinal, "imageOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getIdsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRatingAdvisories", ordinal, "ids");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoRatingAdvisoryIdListTypeAPI getIdsTypeAPI() {
        return getAPI().getVideoRatingAdvisoryIdListTypeAPI();
    }

    public VideoRatingAdvisoriesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}