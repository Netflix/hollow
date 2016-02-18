package com.netflix.vms.transformer.hollowoutput;


public class Integer {

    public int val;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}