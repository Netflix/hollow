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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + vmafScore;
        hashCode = hashCode * 31 + scaledPsnrScore;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("QoEInfo{");
        builder.append("vmafScore=").append(vmafScore);
        builder.append(",scaledPsnrScore=").append(scaledPsnrScore);
        builder.append("}");
        return builder.toString();
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