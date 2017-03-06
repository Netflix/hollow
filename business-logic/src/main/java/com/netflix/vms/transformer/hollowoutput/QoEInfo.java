package com.netflix.vms.transformer.hollowoutput;


public class QoEInfo implements Cloneable {

    public int vmafScore = java.lang.Integer.MIN_VALUE;
    public int scaledPsnrScore = java.lang.Integer.MIN_VALUE;
    public int vmafAlgoVersionExp = java.lang.Integer.MIN_VALUE;
    public int vmafAlgoVersionLts = java.lang.Integer.MIN_VALUE;
    public int vmafScoreExp = java.lang.Integer.MIN_VALUE;
    public int vmafScoreLts = java.lang.Integer.MIN_VALUE;
    public int vmafplusScoreExp = java.lang.Integer.MIN_VALUE;
    public int vmafplusScoreLts = java.lang.Integer.MIN_VALUE;
    public int vmafplusPhoneScoreExp = java.lang.Integer.MIN_VALUE;
    public int vmafplusPhoneScoreLts = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof QoEInfo))
            return false;

        QoEInfo o = (QoEInfo) other;
        if(o.vmafScore != vmafScore) return false;
        if(o.scaledPsnrScore != scaledPsnrScore) return false;
        if(o.vmafAlgoVersionExp != vmafAlgoVersionExp) return false;
        if(o.vmafAlgoVersionLts != vmafAlgoVersionLts) return false;
        if(o.vmafScoreExp != vmafScoreExp) return false;
        if(o.vmafScoreLts != vmafScoreLts) return false;
        if(o.vmafplusScoreExp != vmafplusScoreExp) return false;
        if(o.vmafplusScoreLts != vmafplusScoreLts) return false;
        if(o.vmafplusPhoneScoreExp != vmafplusPhoneScoreExp) return false;
        if(o.vmafplusPhoneScoreLts != vmafplusPhoneScoreLts) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + vmafScore;
        hashCode = hashCode * 31 + scaledPsnrScore;
        hashCode = hashCode * 31 + vmafAlgoVersionExp;
        hashCode = hashCode * 31 + vmafAlgoVersionLts;
        hashCode = hashCode * 31 + vmafScoreExp;
        hashCode = hashCode * 31 + vmafScoreLts;
        hashCode = hashCode * 31 + vmafplusScoreExp;
        hashCode = hashCode * 31 + vmafplusScoreLts;
        hashCode = hashCode * 31 + vmafplusPhoneScoreExp;
        hashCode = hashCode * 31 + vmafplusPhoneScoreLts;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("QoEInfo{");
        builder.append("vmafScore=").append(vmafScore);
        builder.append(",scaledPsnrScore=").append(scaledPsnrScore);
        builder.append(",vmafAlgoVersionExp=").append(vmafAlgoVersionExp);
        builder.append(",vmafAlgoVersionLts=").append(vmafAlgoVersionLts);
        builder.append(",vmafScoreExp=").append(vmafScoreExp);
        builder.append(",vmafScoreLts=").append(vmafScoreLts);
        builder.append(",vmafplusScoreExp=").append(vmafplusScoreExp);
        builder.append(",vmafplusScoreLts=").append(vmafplusScoreLts);
        builder.append(",vmafplusPhoneScoreExp=").append(vmafplusPhoneScoreExp);
        builder.append(",vmafplusPhoneScoreLts=").append(vmafplusPhoneScoreLts);
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