package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class SupplementalVideo implements Cloneable {

    public Video id = null;
    public Video parent = null;
    public int sequenceNumber = java.lang.Integer.MIN_VALUE;
    public int seasonNumber = java.lang.Integer.MIN_VALUE;
    public Map<Strings, Strings> attributes = null;
    public Map<Strings, List<Strings>> multiValueAttributes = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof SupplementalVideo))
            return false;

        SupplementalVideo o = (SupplementalVideo) other;
        if(o.id == null) {
            if(id != null) return false;
        } else if(!o.id.equals(id)) return false;
        if(o.parent == null) {
            if(parent != null) return false;
        } else if(!o.parent.equals(parent)) return false;
        if(o.sequenceNumber != sequenceNumber) return false;
        if(o.seasonNumber != seasonNumber) return false;
        if(o.attributes == null) {
            if(attributes != null) return false;
        } else if(!o.attributes.equals(attributes)) return false;
        if(o.multiValueAttributes == null) {
            if(multiValueAttributes != null) return false;
        } else if(!o.multiValueAttributes.equals(multiValueAttributes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (id == null ? 1237 : id.hashCode());
        hashCode = hashCode * 31 + (parent == null ? 1237 : parent.hashCode());
        hashCode = hashCode * 31 + sequenceNumber;
        hashCode = hashCode * 31 + seasonNumber;
        hashCode = hashCode * 31 + (attributes == null ? 1237 : attributes.hashCode());
        hashCode = hashCode * 31 + (multiValueAttributes == null ? 1237 : multiValueAttributes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("SupplementalVideo{");
        builder.append("id=").append(id);
        builder.append(",parent=").append(parent);
        builder.append(",sequenceNumber=").append(sequenceNumber);
        builder.append(",seasonNumber=").append(seasonNumber);
        builder.append(",attributes=").append(attributes);
        builder.append(",multiValueAttributes=").append(multiValueAttributes);
        builder.append("}");
        return builder.toString();
    }

    public SupplementalVideo clone() {
        try {
            SupplementalVideo clone = (SupplementalVideo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}