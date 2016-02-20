package com.netflix.vms.transformer.hollowoutput;

import java.util.Map;
import java.util.Set;

public class NamedCollectionHolder {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}