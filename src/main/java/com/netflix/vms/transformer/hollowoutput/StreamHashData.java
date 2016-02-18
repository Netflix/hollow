package com.netflix.vms.transformer.hollowoutput;


public class StreamHashData {

    public long cRC32Hash;
    public long sha1_1;
    public long sha1_2;
    public long sha1_3;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamHashData))
            return false;

        StreamHashData o = (StreamHashData) other;
        if(o.cRC32Hash != cRC32Hash) return false;
        if(o.sha1_1 != sha1_1) return false;
        if(o.sha1_2 != sha1_2) return false;
        if(o.sha1_3 != sha1_3) return false;
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}