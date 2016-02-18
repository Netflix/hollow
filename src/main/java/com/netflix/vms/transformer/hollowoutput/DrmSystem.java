package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class DrmSystem {

    public int id;
    public DrmKeyString guid;
    public DrmKeyString name;
    public Map<DrmKeyString, DrmKeyString> attributes;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DrmSystem))
            return false;

        DrmSystem o = (DrmSystem) other;
        if(o.id != id) return false;
        if(o.guid == null) {
            if(guid != null) return false;
        } else if(!o.guid.equals(guid)) return false;
        if(o.name == null) {
            if(name != null) return false;
        } else if(!o.name.equals(name)) return false;
        if(o.attributes == null) {
            if(attributes != null) return false;
        } else if(!o.attributes.equals(attributes)) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}