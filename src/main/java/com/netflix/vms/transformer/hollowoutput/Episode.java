package com.netflix.vms.transformer.hollowoutput;


public class Episode {

    public int id = java.lang.Integer.MIN_VALUE;

    public Episode() { }

    public Episode(int value) {
        this.id = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Episode))
            return false;

        Episode o = (Episode) other;
        if(o.id != id) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}