package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;

public class DrmSystem implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public DrmKeyString guid = null;
    public DrmKeyString name = null;
    public Map<DrmKeyString, DrmKeyString> attributes = null;

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

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (guid == null ? 1237 : guid.hashCode());
        hashCode = hashCode * 31 + (name == null ? 1237 : name.hashCode());
        hashCode = hashCode * 31 + (attributes == null ? 1237 : attributes.hashCode());
        return hashCode;
    }

    public DrmSystem clone() {
        try {
            DrmSystem clone = (DrmSystem)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}