package com.netflix.vms.hollowoutput.pojos;


public class CdnData {

    public int id;
    public Strings name;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}