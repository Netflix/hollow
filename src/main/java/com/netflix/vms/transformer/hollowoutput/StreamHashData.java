package com.netflix.vms.transformer.hollowoutput;


public class StreamHashData implements Cloneable {

    public long cRC32Hash = java.lang.Long.MIN_VALUE;
    public long sha1_1 = java.lang.Long.MIN_VALUE;
    public long sha1_2 = java.lang.Long.MIN_VALUE;
    public long sha1_3 = java.lang.Long.MIN_VALUE;

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

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (cRC32Hash ^ (cRC32Hash >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_1 ^ (sha1_1 >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_2 ^ (sha1_2 >>> 32));
        hashCode = hashCode * 31 + (int) (sha1_3 ^ (sha1_3 >>> 32));
        return hashCode;
    }

    public StreamHashData clone() {
        try {
            StreamHashData clone = (StreamHashData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}