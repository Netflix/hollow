package com.netflix.vms.transformer.hollowoutput;


public class Boolean {

    public boolean val;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}