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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (floatList == null ? 1237 : floatList.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("BasicPassthroughLists{");
        builder.append("floatList=").append(floatList);
        builder.append("}");
        return builder.toString();
    }

    public BasicPassthroughLists clone() {
        try {
            BasicPassthroughLists clone = (BasicPassthroughLists)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}