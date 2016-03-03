package com.netflix.vms.transformer.hollowoutput;


public class CdnData implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public Strings name = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CdnData))
            return false;

        CdnData o = (CdnData) other;
        if(o.id != id) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        return hashCode;
    }

    public CdnData clone() {
        try {
            CdnData clone = (CdnData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}