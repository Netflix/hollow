package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.List;
import java.util.Map;

@HollowPrimaryKey(fields="id")
public class PersonImages implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public Map<Strings, List<Artwork>> artworks = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonImages))
            return false;

        PersonImages o = (PersonImages) other;
        if(o.id != id) return false;
        if(o.artworks == null) {
            if(artworks != null) return false;
        } else if(!o.artworks.equals(artworks)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (artworks == null ? 1237 : artworks.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonImages{");
        builder.append("id=").append(id);
        builder.append(",artworks=").append(artworks);
        builder.append("}");
        return builder.toString();
    }

    public PersonImages clone() {
        try {
            PersonImages clone = (PersonImages)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}