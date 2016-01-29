package com.netflix.vms.hollowoutput.pojos;


public class CupKey {

    public Strings token;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}