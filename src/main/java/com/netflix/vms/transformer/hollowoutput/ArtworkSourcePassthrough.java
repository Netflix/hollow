package com.netflix.vms.transformer.hollowoutput;


public class ArtworkSourcePassthrough implements Cloneable {

    public ArtworkSourceString source_file_id = null;
    public ArtworkSourceString original_source_file_id = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkSourcePassthrough))
            return false;

        ArtworkSourcePassthrough o = (ArtworkSourcePassthrough) other;
        if(o.source_file_id == null) {
            if(source_file_id != null) return false;
        } else if(!o.source_file_id.equals(source_file_id)) return false;
        if(o.original_source_file_id == null) {
            if(original_source_file_id != null) return false;
        } else if(!o.original_source_file_id.equals(original_source_file_id)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 0;
        hashCode = hashCode * 31 + (source_file_id == null ? 1237 : source_file_id.hashCode());
        hashCode = hashCode * 31 + (original_source_file_id == null ? 1237 : original_source_file_id.hashCode());
        return hashCode;
    }

    public ArtworkSourcePassthrough clone() {
        try {
            ArtworkSourcePassthrough clone = (ArtworkSourcePassthrough)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}