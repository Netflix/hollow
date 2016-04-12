package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class PersonImages implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public List<ArtWorkDescriptor> artWorkDescriptors = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PersonImages))
            return false;

        PersonImages o = (PersonImages) other;
        if(o.id != id) return false;
        if(o.artWorkDescriptors == null) {
            if(artWorkDescriptors != null) return false;
        } else if(!o.artWorkDescriptors.equals(artWorkDescriptors)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (artWorkDescriptors == null ? 1237 : artWorkDescriptors.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PersonImages{");
        builder.append("id=").append(id);
        builder.append(",artWorkDescriptors=").append(artWorkDescriptors);
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