package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="VideoStreamInfo")
public class VideoStreamInfo implements Cloneable {

    public int videoBitrateKBPS = java.lang.Integer.MIN_VALUE;
    public int videoPeakBitrateKBPS = java.lang.Integer.MIN_VALUE;
    public long dashHeaderSize = java.lang.Long.MIN_VALUE;
    public long dashMediaStartByteOffset = java.lang.Long.MIN_VALUE;
    public DashStreamHeaderData dashStreamHeaderData = null;
    public long vmafScore = java.lang.Long.MIN_VALUE;
    public int vmafAlgoVersionExp = java.lang.Integer.MIN_VALUE;
    public int vmafAlgoVersionLts = java.lang.Integer.MIN_VALUE;
    public int vmafScoreExp = java.lang.Integer.MIN_VALUE;
    public int vmafScoreLts = java.lang.Integer.MIN_VALUE;
    public int vmafplusScoreExp = java.lang.Integer.MIN_VALUE;
    public int vmafplusScoreLts = java.lang.Integer.MIN_VALUE;
    public int vmafplusPhoneScoreExp = java.lang.Integer.MIN_VALUE;
    public int vmafplusPhoneScoreLts = java.lang.Integer.MIN_VALUE;
    public long scaledPsnrTimesHundred = java.lang.Long.MIN_VALUE;
    public float fps = java.lang.Float.NaN;
    public VideoStreamCropParams cropParams = null;

    public VideoStreamInfo setVideoBitrateKBPS(int videoBitrateKBPS) {
        this.videoBitrateKBPS = videoBitrateKBPS;
        return this;
    }
    public VideoStreamInfo setVideoPeakBitrateKBPS(int videoPeakBitrateKBPS) {
        this.videoPeakBitrateKBPS = videoPeakBitrateKBPS;
        return this;
    }
    public VideoStreamInfo setDashHeaderSize(long dashHeaderSize) {
        this.dashHeaderSize = dashHeaderSize;
        return this;
    }
    public VideoStreamInfo setDashMediaStartByteOffset(long dashMediaStartByteOffset) {
        this.dashMediaStartByteOffset = dashMediaStartByteOffset;
        return this;
    }
    public VideoStreamInfo setDashStreamHeaderData(DashStreamHeaderData dashStreamHeaderData) {
        this.dashStreamHeaderData = dashStreamHeaderData;
        return this;
    }
    public VideoStreamInfo setVmafScore(long vmafScore) {
        this.vmafScore = vmafScore;
        return this;
    }
    public VideoStreamInfo setVmafAlgoVersionExp(int vmafAlgoVersionExp) {
        this.vmafAlgoVersionExp = vmafAlgoVersionExp;
        return this;
    }
    public VideoStreamInfo setVmafAlgoVersionLts(int vmafAlgoVersionLts) {
        this.vmafAlgoVersionLts = vmafAlgoVersionLts;
        return this;
    }
    public VideoStreamInfo setVmafScoreExp(int vmafScoreExp) {
        this.vmafScoreExp = vmafScoreExp;
        return this;
    }
    public VideoStreamInfo setVmafScoreLts(int vmafScoreLts) {
        this.vmafScoreLts = vmafScoreLts;
        return this;
    }
    public VideoStreamInfo setVmafplusScoreExp(int vmafplusScoreExp) {
        this.vmafplusScoreExp = vmafplusScoreExp;
        return this;
    }
    public VideoStreamInfo setVmafplusScoreLts(int vmafplusScoreLts) {
        this.vmafplusScoreLts = vmafplusScoreLts;
        return this;
    }
    public VideoStreamInfo setVmafplusPhoneScoreExp(int vmafplusPhoneScoreExp) {
        this.vmafplusPhoneScoreExp = vmafplusPhoneScoreExp;
        return this;
    }
    public VideoStreamInfo setVmafplusPhoneScoreLts(int vmafplusPhoneScoreLts) {
        this.vmafplusPhoneScoreLts = vmafplusPhoneScoreLts;
        return this;
    }
    public VideoStreamInfo setScaledPsnrTimesHundred(long scaledPsnrTimesHundred) {
        this.scaledPsnrTimesHundred = scaledPsnrTimesHundred;
        return this;
    }
    public VideoStreamInfo setFps(float fps) {
        this.fps = fps;
        return this;
    }
    public VideoStreamInfo setCropParams(VideoStreamCropParams cropParams) {
        this.cropParams = cropParams;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoStreamInfo))
            return false;

        VideoStreamInfo o = (VideoStreamInfo) other;
        if(o.videoBitrateKBPS != videoBitrateKBPS) return false;
        if(o.videoPeakBitrateKBPS != videoPeakBitrateKBPS) return false;
        if(o.dashHeaderSize != dashHeaderSize) return false;
        if(o.dashMediaStartByteOffset != dashMediaStartByteOffset) return false;
        if(o.dashStreamHeaderData == null) {
            if(dashStreamHeaderData != null) return false;
        } else if(!o.dashStreamHeaderData.equals(dashStreamHeaderData)) return false;
        if(o.vmafScore != vmafScore) return false;
        if(o.vmafAlgoVersionExp != vmafAlgoVersionExp) return false;
        if(o.vmafAlgoVersionLts != vmafAlgoVersionLts) return false;
        if(o.vmafScoreExp != vmafScoreExp) return false;
        if(o.vmafScoreLts != vmafScoreLts) return false;
        if(o.vmafplusScoreExp != vmafplusScoreExp) return false;
        if(o.vmafplusScoreLts != vmafplusScoreLts) return false;
        if(o.vmafplusPhoneScoreExp != vmafplusPhoneScoreExp) return false;
        if(o.vmafplusPhoneScoreLts != vmafplusPhoneScoreLts) return false;
        if(o.scaledPsnrTimesHundred != scaledPsnrTimesHundred) return false;
        if(o.fps != fps) return false;
        if(o.cropParams == null) {
            if(cropParams != null) return false;
        } else if(!o.cropParams.equals(cropParams)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + videoBitrateKBPS;
        hashCode = hashCode * 31 + videoPeakBitrateKBPS;
        hashCode = hashCode * 31 + (int) (dashHeaderSize ^ (dashHeaderSize >>> 32));
        hashCode = hashCode * 31 + (int) (dashMediaStartByteOffset ^ (dashMediaStartByteOffset >>> 32));
        hashCode = hashCode * 31 + (dashStreamHeaderData == null ? 1237 : dashStreamHeaderData.hashCode());
        hashCode = hashCode * 31 + (int) (vmafScore ^ (vmafScore >>> 32));
        hashCode = hashCode * 31 + vmafAlgoVersionExp;
        hashCode = hashCode * 31 + vmafAlgoVersionLts;
        hashCode = hashCode * 31 + vmafScoreExp;
        hashCode = hashCode * 31 + vmafScoreLts;
        hashCode = hashCode * 31 + vmafplusScoreExp;
        hashCode = hashCode * 31 + vmafplusScoreLts;
        hashCode = hashCode * 31 + vmafplusPhoneScoreExp;
        hashCode = hashCode * 31 + vmafplusPhoneScoreLts;
        hashCode = hashCode * 31 + (int) (scaledPsnrTimesHundred ^ (scaledPsnrTimesHundred >>> 32));
        hashCode = hashCode * 31 + java.lang.Float.floatToIntBits(fps);
        hashCode = hashCode * 31 + (cropParams == null ? 1237 : cropParams.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoStreamInfo{");
        builder.append("videoBitrateKBPS=").append(videoBitrateKBPS);
        builder.append(",videoPeakBitrateKBPS=").append(videoPeakBitrateKBPS);
        builder.append(",dashHeaderSize=").append(dashHeaderSize);
        builder.append(",dashMediaStartByteOffset=").append(dashMediaStartByteOffset);
        builder.append(",dashStreamHeaderData=").append(dashStreamHeaderData);
        builder.append(",vmafScore=").append(vmafScore);
        builder.append(",vmafAlgoVersionExp=").append(vmafAlgoVersionExp);
        builder.append(",vmafAlgoVersionLts=").append(vmafAlgoVersionLts);
        builder.append(",vmafScoreExp=").append(vmafScoreExp);
        builder.append(",vmafScoreLts=").append(vmafScoreLts);
        builder.append(",vmafplusScoreExp=").append(vmafplusScoreExp);
        builder.append(",vmafplusScoreLts=").append(vmafplusScoreLts);
        builder.append(",vmafplusPhoneScoreExp=").append(vmafplusPhoneScoreExp);
        builder.append(",vmafplusPhoneScoreLts=").append(vmafplusPhoneScoreLts);
        builder.append(",scaledPsnrTimesHundred=").append(scaledPsnrTimesHundred);
        builder.append(",fps=").append(fps);
        builder.append(",cropParams=").append(cropParams);
        builder.append("}");
        return builder.toString();
    }

    public VideoStreamInfo clone() {
        try {
            VideoStreamInfo clone = (VideoStreamInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}