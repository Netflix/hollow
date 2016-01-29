package com.netflix.vms.hollowoutput.pojos;


public class VideoDimensionsDescriptor {

    public int dimensions;
    public Strings name;
    public Strings description;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoDimensionsDescriptor))
            return false;

        VideoDimensionsDescriptor o = (VideoDimensionsDescriptor) other;
        if(o.dimensions != dimensions) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}