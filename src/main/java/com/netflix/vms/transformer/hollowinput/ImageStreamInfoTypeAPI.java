package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class ImageStreamInfoTypeAPI extends HollowObjectTypeAPI {

    private final ImageStreamInfoDelegateLookupImpl delegateLookupImpl;

    ImageStreamInfoTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "imageCount",
            "imageFormat",
            "offsetMillis"
        });
        this.delegateLookupImpl = new ImageStreamInfoDelegateLookupImpl(this);
    }

    public int getImageCount(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("ImageStreamInfo", ordinal, "imageCount");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getImageCountBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("ImageStreamInfo", ordinal, "imageCount");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getImageFormatOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("ImageStreamInfo", ordinal, "imageFormat");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getImageFormatTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getOffsetMillis(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("ImageStreamInfo", ordinal, "offsetMillis");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getOffsetMillisBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("ImageStreamInfo", ordinal, "offsetMillis");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public ImageStreamInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}