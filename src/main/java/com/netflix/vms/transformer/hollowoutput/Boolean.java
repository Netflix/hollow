package com.netflix.vms.transformer.hollowoutput;


public class Boolean implements Cloneable {

    public boolean val = false;

    public Boolean() { }

    public Boolean(boolean value) {
        this.val = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Boolean))
            return false;

        Boolean o = (Boolean) other;
        if(o.val != val) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (val? 1231 : 1237);
        return hashCode;
    }

    public Boolean clone() {
        try {
            Boolean clone = (Boolean)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}