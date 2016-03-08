package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class CharacterImages implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public List<ArtWorkDescriptor> artWorkDescriptorList = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CharacterImages))
            return false;

        CharacterImages o = (CharacterImages) other;
        if(o.id != id) return false;
        if(o.artWorkDescriptorList == null) {
            if(artWorkDescriptorList != null) return false;
        } else if(!o.artWorkDescriptorList.equals(artWorkDescriptorList)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + (artWorkDescriptorList == null ? 1237 : artWorkDescriptorList.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CharacterImages{");
        builder.append("id=").append(id);
        builder.append(",artWorkDescriptorList=").append(artWorkDescriptorList);
        builder.append("}");
        return builder.toString();
    }

    public CharacterImages clone() {
        try {
            CharacterImages clone = (CharacterImages)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}