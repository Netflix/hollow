package com.netflix.vms.transformer.hollowoutput;


public class CupKey implements Cloneable {

    public Strings token = null;

    public CupKey() { }

    public CupKey(Strings value) {
        this.token = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CupKey))
            return false;

        CupKey o = (CupKey) other;
        if(o.token == null) {
            if(token != null) return false;
        } else if(!o.token.equals(token)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (token == null ? 1237 : token.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CupKey{");
        builder.append("token=").append(token);
        builder.append("}");
        return builder.toString();
    }

    public CupKey clone() {
        try {
            CupKey clone = (CupKey)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}