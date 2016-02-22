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

    public ArtworkSourcePassthrough clone() {
        try {
            return (ArtworkSourcePassthrough)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}