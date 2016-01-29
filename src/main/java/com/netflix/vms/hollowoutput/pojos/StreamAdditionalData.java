package com.netflix.vms.hollowoutput.pojos;


public class StreamAdditionalData {

    public FrameRate frameRate;
    public DownloadLocationSet downloadLocations;
    public QoEInfo qoeInfo;
    public StreamMostlyConstantData mostlyConstantData;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}