package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class StreamDimensionsTypeAPI extends HollowObjectTypeAPI {

    private final StreamDimensionsDelegateLookupImpl delegateLookupImpl;

    StreamDimensionsTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "widthInPixels",
            "heightInPixels",
            "pixelAspectRatioWidth",
            "pixelAspectRatioHeight",
            "targetWidthInPixels",
            "targetHeightInPixels"
        });
        this.delegateLookupImpl = new StreamDimensionsDelegateLookupImpl(this);
    }

    public int getWidthInPixels(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("StreamDimensions", ordinal, "widthInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getWidthInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("StreamDimensions", ordinal, "widthInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getHeightInPixels(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("StreamDimensions", ordinal, "heightInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getHeightInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("StreamDimensions", ordinal, "heightInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getPixelAspectRatioWidth(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleInt("StreamDimensions", ordinal, "pixelAspectRatioWidth");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
    }

    public Integer getPixelAspectRatioWidthBoxed(int ordinal) {
        int i;
        if(fieldIndex[2] == -1) {
            i = missingDataHandler().handleInt("StreamDimensions", ordinal, "pixelAspectRatioWidth");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[2]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getPixelAspectRatioHeight(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleInt("StreamDimensions", ordinal, "pixelAspectRatioHeight");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
    }

    public Integer getPixelAspectRatioHeightBoxed(int ordinal) {
        int i;
        if(fieldIndex[3] == -1) {
            i = missingDataHandler().handleInt("StreamDimensions", ordinal, "pixelAspectRatioHeight");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[3]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getTargetWidthInPixels(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleInt("StreamDimensions", ordinal, "targetWidthInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
    }

    public Integer getTargetWidthInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[4] == -1) {
            i = missingDataHandler().handleInt("StreamDimensions", ordinal, "targetWidthInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[4]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getTargetHeightInPixels(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleInt("StreamDimensions", ordinal, "targetHeightInPixels");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[5]);
    }

    public Integer getTargetHeightInPixelsBoxed(int ordinal) {
        int i;
        if(fieldIndex[5] == -1) {
            i = missingDataHandler().handleInt("StreamDimensions", ordinal, "targetHeightInPixels");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[5]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public StreamDimensionsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}