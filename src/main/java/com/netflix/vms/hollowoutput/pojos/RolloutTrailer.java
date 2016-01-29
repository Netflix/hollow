package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;

public class RolloutTrailer {

    public Video video;
    public int sequenceNumber;
    public Map<SupplementalInfoType, TrailerInfo> supplementalInfos;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof RolloutTrailer))
            return false;

        RolloutTrailer o = (RolloutTrailer) other;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.supplementalInfos == null) {
            if(supplementalInfos != null) return false;
        } else if(!o.supplementalInfos.equals(supplementalInfos)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}