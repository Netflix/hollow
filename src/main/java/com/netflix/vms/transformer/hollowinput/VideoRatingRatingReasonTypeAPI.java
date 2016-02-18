package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRatingRatingReasonTypeAPI extends HollowObjectTypeAPI {

    private final VideoRatingRatingReasonDelegateLookupImpl delegateLookupImpl;

    VideoRatingRatingReasonTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "ordered",
            "imageOnly",
            "ids"
        });
        this.delegateLookupImpl = new VideoRatingRatingReasonDelegateLookupImpl(this);
    }

    public boolean getOrdered(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRatingRatingReason", ordinal, "ordered") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getOrderedBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("VideoRatingRatingReason", ordinal, "ordered");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }



    public boolean getImageOnly(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRatingRatingReason", ordinal, "imageOnly") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]) == Boolean.TRUE;
    }

    public Boolean getImageOnlyBoxed(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleBoolean("VideoRatingRatingReason", ordinal, "imageOnly");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[1]);
    }



    public int getIdsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRatingRatingReason", ordinal, "ids");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public VideoRatingRatingReasonArrayOfIdsTypeAPI getIdsTypeAPI() {
        return getAPI().getVideoRatingRatingReasonArrayOfIdsTypeAPI();
    }

    public VideoRatingRatingReasonDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}