package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="AltGenres")
public class AltGenres implements Cloneable {

    public long altGenreId = java.lang.Long.MIN_VALUE;
    public TranslatedText displayName = null;
    public TranslatedText shortName = null;
    @HollowTypeName(name="AltGenresAlternateNamesList")
    public List<AltGenresAlternateNames> alternateNames = null;

    public AltGenres setAltGenreId(long altGenreId) {
        this.altGenreId = altGenreId;
        return this;
    }
    public AltGenres setDisplayName(TranslatedText displayName) {
        this.displayName = displayName;
        return this;
    }
    public AltGenres setShortName(TranslatedText shortName) {
        this.shortName = shortName;
        return this;
    }
    public AltGenres setAlternateNames(List<AltGenresAlternateNames> alternateNames) {
        this.alternateNames = alternateNames;
        return this;
    }
    public AltGenres addToAlternateNames(AltGenresAlternateNames altGenresAlternateNames) {
        if (this.alternateNames == null) {
            this.alternateNames = new ArrayList<AltGenresAlternateNames>();
        }
        this.alternateNames.add(altGenresAlternateNames);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof AltGenres))
            return false;

        AltGenres o = (AltGenres) other;
        if(o.altGenreId != altGenreId) return false;
        if(o.displayName == null) {
            if(displayName != null) return false;
        } else if(!o.displayName.equals(displayName)) return false;
        if(o.shortName == null) {
            if(shortName != null) return false;
        } else if(!o.shortName.equals(shortName)) return false;
        if(o.alternateNames == null) {
            if(alternateNames != null) return false;
        } else if(!o.alternateNames.equals(alternateNames)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (altGenreId ^ (altGenreId >>> 32));
        hashCode = hashCode * 31 + (displayName == null ? 1237 : displayName.hashCode());
        hashCode = hashCode * 31 + (shortName == null ? 1237 : shortName.hashCode());
        hashCode = hashCode * 31 + (alternateNames == null ? 1237 : alternateNames.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("AltGenres{");
        builder.append("altGenreId=").append(altGenreId);
        builder.append(",displayName=").append(displayName);
        builder.append(",shortName=").append(shortName);
        builder.append(",alternateNames=").append(alternateNames);
        builder.append("}");
        return builder.toString();
    }

    public AltGenres clone() {
        try {
            AltGenres clone = (AltGenres)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}