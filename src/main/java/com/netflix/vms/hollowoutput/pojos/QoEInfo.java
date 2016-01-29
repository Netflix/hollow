package com.netflix.vms.hollowoutput.pojos;


public class QoEInfo {

    public int vmafScore;
    public int scaledPsnrScore;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof QoEInfo))
            return false;

        QoEInfo o = (QoEInfo) other;
        if(o.vmafScore != vmafScore) return false;
        if(o.scaledPsnrScore != scaledPsnrScore) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}