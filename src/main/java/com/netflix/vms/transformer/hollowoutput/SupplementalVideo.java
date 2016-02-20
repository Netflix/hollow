package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;

public class SupplementalVideo {

    public Video id;
    public Video parent;
    public int sequenceNumber;
    public int seasonNumber;
    public Map<Strings, Strings> attributes;
    public Map<Strings, List<Strings>> multiValueAttributes;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}