package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class VideoStreamCropParamsTypeAPI extends HollowObjectTypeAPI {

    private final VideoStreamCropParamsDelegateLookupImpl delegateLookupImpl;

    public VideoStreamCropParamsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "x",
            "y",
            "width",
            "height"
        });
        this.delegateLookupImpl = new VideoStreamCropParamsDelegateLookupImpl(this);
    }

    public int getX(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "x");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getXBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "x");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getY(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "y");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getYBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "y");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getWidth(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "width");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getWidthBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "width");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getHeight(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "height");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getHeightBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("VideoStreamCropParams", ordinal, "height");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public VideoStreamCropParamsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}