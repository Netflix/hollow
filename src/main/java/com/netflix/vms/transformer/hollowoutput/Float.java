package com.netflix.vms.transformer.hollowoutput;


public class Float implements Cloneable {

    public float val = java.lang.Float.NaN;

    public Float() { }

    public Float(float value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Float))
            return false;

        Float o = (Float) other;
        if(o.val != val) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + java.lang.Float.floatToIntBits(val);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Float{");
        builder.append("val=").append(val);
        builder.append("}");
        return builder.toString();
    }

    public Float clone() {
        try {
            Float clone = (Float)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}