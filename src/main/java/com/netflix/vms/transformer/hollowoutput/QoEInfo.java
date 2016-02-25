package com.netflix.vms.transformer.hollowoutput;


public class QoEInfo implements Cloneable {

    public int vmafScore = java.lang.Integer.MIN_VALUE;
    public int scaledPsnrScore = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof QoEInfo))
            return false;

        QoEInfo o = (QoEInfo) other;
        if(o.vmafScore != vmafScore) return false;
        if(o.scaledPsnrScore != scaledPsnrScore) return false;
        return true;
    }

    public QoEInfo clone() {
        try {
            QoEInfo clone = (QoEInfo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}