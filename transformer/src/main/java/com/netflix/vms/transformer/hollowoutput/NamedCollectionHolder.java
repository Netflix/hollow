package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;
import java.util.Set;

public class NamedCollectionHolder implements Cloneable {

    public ISOCountry country = null;
    public Map<Strings, Set<Video>> videoListMap = null;
    public Map<Strings, Set<VPerson>> personListMap = null;
    public Map<Strings, Set<Episode>> episodeListMap = null;
    public Map<Strings, Set<NFResourceID>> resourceIdListMap = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof NamedCollectionHolder))
            return false;

        NamedCollectionHolder o = (NamedCollectionHolder) other;
        if(o.country == null) {
            if(country != null) return false;
        } else if(!o.country.equals(country)) return false;
        if(o.videoListMap == null) {
            if(videoListMap != null) return false;
        } else if(!o.videoListMap.equals(videoListMap)) return false;
        if(o.personListMap == null) {
            if(personListMap != null) return false;
        } else if(!o.personListMap.equals(personListMap)) return false;
        if(o.episodeListMap == null) {
            if(episodeListMap != null) return false;
        } else if(!o.episodeListMap.equals(episodeListMap)) return false;
        if(o.resourceIdListMap == null) {
            if(resourceIdListMap != null) return false;
        } else if(!o.resourceIdListMap.equals(resourceIdListMap)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (country == null ? 1237 : country.hashCode());
        hashCode = hashCode * 31 + (videoListMap == null ? 1237 : videoListMap.hashCode());
        hashCode = hashCode * 31 + (personListMap == null ? 1237 : personListMap.hashCode());
        hashCode = hashCode * 31 + (episodeListMap == null ? 1237 : episodeListMap.hashCode());
        hashCode = hashCode * 31 + (resourceIdListMap == null ? 1237 : resourceIdListMap.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("NamedCollectionHolder{");
        builder.append("country=").append(country);
        builder.append(",videoListMap=").append(videoListMap);
        builder.append(",personListMap=").append(personListMap);
        builder.append(",episodeListMap=").append(episodeListMap);
        builder.append(",resourceIdListMap=").append(resourceIdListMap);
        builder.append("}");
        return builder.toString();
    }

    public NamedCollectionHolder clone() {
        try {
            NamedCollectionHolder clone = (NamedCollectionHolder)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}