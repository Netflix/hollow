package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class BasicPassthroughLists implements Cloneable {

    public List<SuperFloat> floatList = null;

    public BasicPassthroughLists() { }

    public BasicPassthroughLists(List<SuperFloat> value) {
        this.floatList = value;
    }

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof BasicPassthroughLists))
            return false;

        BasicPassthroughLists o = (BasicPassthroughLists) other;
        if(o.floatList == null) {
            if(floatList != null) return false;
        } else if(!o.floatList.equals(floatList)) return false;
        return true;
    }

    public BasicPassthroughLists clone() {
        try {
            return (BasicPassthroughLists)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}