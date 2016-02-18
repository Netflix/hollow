package com.netflix.vms.transformer.hollowoutput;


public class ArtworkSourcePassthrough {

    public ArtworkSourceString source_file_id;
    public ArtworkSourceString original_source_file_id;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}