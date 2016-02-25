package com.netflix.vms.transformer.hollowoutput;


public class StreamAdditionalData implements Cloneable {

    public FrameRate frameRate = null;
    public DownloadLocationSet downloadLocations = null;
    public QoEInfo qoeInfo = null;
    public StreamMostlyConstantData mostlyConstantData = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamAdditionalData))
            return false;

        StreamAdditionalData o = (StreamAdditionalData) other;
        if(o.frameRate == null) {
            if(frameRate != null) return false;
        } else if(!o.frameRate.equals(frameRate)) return false;
        if(o.downloadLocations == null) {
            if(downloadLocations != null) return false;
        } else if(!o.downloadLocations.equals(downloadLocations)) return false;
        if(o.qoeInfo == null) {
            if(qoeInfo != null) return false;
        } else if(!o.qoeInfo.equals(qoeInfo)) return false;
        if(o.mostlyConstantData == null) {
            if(mostlyConstantData != null) return false;
        } else if(!o.mostlyConstantData.equals(mostlyConstantData)) return false;
        return true;
    }

    public StreamAdditionalData clone() {
        try {
            StreamAdditionalData clone = (StreamAdditionalData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}