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

    public CupKey clone() {
        try {
            return (CupKey)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}