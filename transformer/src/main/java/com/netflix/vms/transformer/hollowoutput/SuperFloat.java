package com.netflix.vms.transformer.hollowoutput;


public class SuperFloat implements Cloneable {

    public float value = java.lang.Float.NaN;

    public SuperFloat() { }

    public SuperFloat(float value) {
        this.value = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof SuperFloat))
            return false;

        SuperFloat o = (SuperFloat) other;
        if(o.value != value) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + java.lang.Float.floatToIntBits(value);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("SuperFloat{");
        builder.append("value=").append(value);
        builder.append("}");
        return builder.toString();
    }

    public SuperFloat clone() {
        try {
            SuperFloat clone = (SuperFloat)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}