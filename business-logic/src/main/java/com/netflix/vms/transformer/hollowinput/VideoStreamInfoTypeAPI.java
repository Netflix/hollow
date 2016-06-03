package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.write.HollowObjectWriteRecord;

public class VideoStreamInfoTypeAPI extends HollowObjectTypeAPI {

    private final VideoStreamInfoDelegateLookupImpl delegateLookupImpl;

    VideoStreamInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoBitrateKBPS",
            "dashHeaderSize",
            "dashMediaStartByteOffset",
            "vmafScore",
            "scaledPsnrTimesHundred",
            "fps"
        });
        this.delegateLookupImpl = new VideoStreamInfoDelegateLookupImpl(this);
    }

    public int getVideoBitrateKBPS(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "videoBitrateKBPS");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getVideoBitrateKBPSBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "videoBitrateKBPS");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public long getDashHeaderSize(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashHeaderSize");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getDashHeaderSizeBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashHeaderSize");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getDashMediaStartByteOffset(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashMediaStartByteOffset");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getDashMediaStartByteOffsetBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashMediaStartByteOffset");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getVmafScore(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "vmafScore");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getVmafScoreBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "vmafScore");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getScaledPsnrTimesHundred(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "scaledPsnrTimesHundred");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getScaledPsnrTimesHundredBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "scaledPsnrTimesHundred");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public float getFps(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleFloat("VideoStreamInfo", ordinal, "fps");
        return getTypeDataAccess().readFloat(ordinal, fieldIndex[5]);
    }

    public Float getFpsBoxed(int ordinal) {
        float f;
        if(fieldIndex[5] == -1) {
            f = missingDataHandler().handleFloat("VideoStreamInfo", ordinal, "fps");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            f = getTypeDataAccess().readFloat(ordinal, fieldIndex[5]);
        }        return Float.isNaN(f) ? null : Float.valueOf(f);
    }



    public VideoStreamInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}