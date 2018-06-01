package com.netflix.vms.transformer.hollowoutput;


public class Integer implements Cloneable {

    public int val = java.lang.Integer.MIN_VALUE;

    public Integer() { }

    public Integer(int value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Integer))
            return false;

        Integer o = (Integer) other;
        if(o.val != val) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + val;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Integer{");
        builder.append("val=").append(val);
        builder.append("}");
        return builder.toString();
    }

    public Integer clone() {
        try {
            Integer clone = (Integer)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
