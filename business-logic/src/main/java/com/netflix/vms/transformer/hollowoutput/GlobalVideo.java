package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.util.List;
import java.util.Set;

@HollowPrimaryKey(fields="completeVideo.id")
public class GlobalVideo implements Cloneable {

    public CompleteVideo completeVideo = null;
    public Set<Strings> aliases = null;
    public Set<ISOCountry> availableCountries = null;
    public boolean isSupplementalVideo = false;
    public List<MoviePersonCharacter> personCharacters = null;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (completeVideo == null ? 1237 : completeVideo.hashCode());
        hashCode = hashCode * 31 + (aliases == null ? 1237 : aliases.hashCode());
        hashCode = hashCode * 31 + (availableCountries == null ? 1237 : availableCountries.hashCode());
        hashCode = hashCode * 31 + (isSupplementalVideo? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("GlobalVideo{");
        builder.append("completeVideo=").append(completeVideo);
        builder.append(",aliases=").append(aliases);
        builder.append(",availableCountries=").append(availableCountries);
        builder.append(",isSupplementalVideo=").append(isSupplementalVideo);
        builder.append("}");
        return builder.toString();
    }

    public GlobalVideo clone() {
        try {
            GlobalVideo clone = (GlobalVideo)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}