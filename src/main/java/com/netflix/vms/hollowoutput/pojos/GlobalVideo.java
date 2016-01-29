package com.netflix.vms.hollowoutput.pojos;

import java.util.Set;

public class GlobalVideo {

    public CompleteVideo completeVideo;
    public Set<Strings> aliases;
    public Set<ISOCountry> availableCountries;
    public boolean isSupplementalVideo;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof GlobalVideo))
            return false;

        GlobalVideo o = (GlobalVideo) other;
        if(o.completeVideo == null) {
            if(completeVideo != null) return false;
        } else if(!o.completeVideo.equals(completeVideo)) return false;
        if(o.aliases == null) {
            if(aliases != null) return false;
        } else if(!o.aliases.equals(aliases)) return false;
        if(o.availableCountries == null) {
            if(availableCountries != null) return false;
        } else if(!o.availableCountries.equals(availableCountries)) return false;
        if(o.isSupplementalVideo != isSupplementalVideo) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}