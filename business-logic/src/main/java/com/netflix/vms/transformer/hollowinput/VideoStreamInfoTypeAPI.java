package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;

@SuppressWarnings("all")
public class VideoStreamInfoTypeAPI extends HollowObjectTypeAPI {

    private final VideoStreamInfoDelegateLookupImpl delegateLookupImpl;

    VideoStreamInfoTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoBitrateKBPS",
            "videoPeakBitrateKBPS",
            "dashHeaderSize",
            "dashMediaStartByteOffset",
            "dashStreamHeaderData",
            "vmafScore",
            "vmafAlgoVersionExp",
            "vmafAlgoVersionLts",
            "vmafScoreExp",
            "vmafScoreLts",
            "vmafplusScoreExp",
            "vmafplusScoreLts",
            "vmafplusPhoneScoreExp",
            "vmafplusPhoneScoreLts",
            "scaledPsnrTimesHundred",
            "fps",
            "cropParams"
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



    public int getVideoPeakBitrateKBPS(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "videoPeakBitrateKBPS");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
    }

    public Integer getVideoPeakBitrateKBPSBoxed(int ordinal) {
        int i;
        if(fieldIndex[1] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "videoPeakBitrateKBPS");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[1]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public long getDashHeaderSize(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashHeaderSize");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getDashHeaderSizeBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashHeaderSize");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getDashMediaStartByteOffset(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashMediaStartByteOffset");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getDashMediaStartByteOffsetBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "dashMediaStartByteOffset");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDashStreamHeaderDataOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoStreamInfo", ordinal, "dashStreamHeaderData");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public DashStreamHeaderDataTypeAPI getDashStreamHeaderDataTypeAPI() {
        return getAPI().getDashStreamHeaderDataTypeAPI();
    }

    public long getVmafScore(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "vmafScore");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
    }

    public Long getVmafScoreBoxed(int ordinal) {
        long l;
        if(fieldIndex[5] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "vmafScore");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getVmafAlgoVersionExp(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafAlgoVersionExp");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[6]);
    }

    public Integer getVmafAlgoVersionExpBoxed(int ordinal) {
        int i;
        if(fieldIndex[6] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafAlgoVersionExp");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[6]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[6]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafAlgoVersionLts(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafAlgoVersionLts");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[7]);
    }

    public Integer getVmafAlgoVersionLtsBoxed(int ordinal) {
        int i;
        if(fieldIndex[7] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafAlgoVersionLts");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[7]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[7]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafScoreExp(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafScoreExp");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[8]);
    }

    public Integer getVmafScoreExpBoxed(int ordinal) {
        int i;
        if(fieldIndex[8] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafScoreExp");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[8]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafScoreLts(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafScoreLts");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[9]);
    }

    public Integer getVmafScoreLtsBoxed(int ordinal) {
        int i;
        if(fieldIndex[9] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafScoreLts");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[9]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[9]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafplusScoreExp(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusScoreExp");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[10]);
    }

    public Integer getVmafplusScoreExpBoxed(int ordinal) {
        int i;
        if(fieldIndex[10] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusScoreExp");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[10]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[10]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafplusScoreLts(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusScoreLts");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[11]);
    }

    public Integer getVmafplusScoreLtsBoxed(int ordinal) {
        int i;
        if(fieldIndex[11] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusScoreLts");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[11]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[11]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafplusPhoneScoreExp(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusPhoneScoreExp");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[12]);
    }

    public Integer getVmafplusPhoneScoreExpBoxed(int ordinal) {
        int i;
        if(fieldIndex[12] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusPhoneScoreExp");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[12]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[12]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public int getVmafplusPhoneScoreLts(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusPhoneScoreLts");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[13]);
    }

    public Integer getVmafplusPhoneScoreLtsBoxed(int ordinal) {
        int i;
        if(fieldIndex[13] == -1) {
            i = missingDataHandler().handleInt("VideoStreamInfo", ordinal, "vmafplusPhoneScoreLts");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[13]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[13]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public long getScaledPsnrTimesHundred(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleLong("VideoStreamInfo", ordinal, "scaledPsnrTimesHundred");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[14]);
    }

    public Long getScaledPsnrTimesHundredBoxed(int ordinal) {
        long l;
        if(fieldIndex[14] == -1) {
            l = missingDataHandler().handleLong("VideoStreamInfo", ordinal, "scaledPsnrTimesHundred");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[14]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[14]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public float getFps(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleFloat("VideoStreamInfo", ordinal, "fps");
        return getTypeDataAccess().readFloat(ordinal, fieldIndex[15]);
    }

    public Float getFpsBoxed(int ordinal) {
        float f;
        if(fieldIndex[15] == -1) {
            f = missingDataHandler().handleFloat("VideoStreamInfo", ordinal, "fps");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[15]);
            f = getTypeDataAccess().readFloat(ordinal, fieldIndex[15]);
        }        return Float.isNaN(f) ? null : Float.valueOf(f);
    }



    public int getCropParamsOrdinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoStreamInfo", ordinal, "cropParams");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public VideoStreamCropParamsTypeAPI getCropParamsTypeAPI() {
        return getAPI().getVideoStreamCropParamsTypeAPI();
    }

    public VideoStreamInfoDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}