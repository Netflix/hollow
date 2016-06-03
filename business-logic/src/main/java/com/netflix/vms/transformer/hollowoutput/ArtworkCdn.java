package com.netflix.vms.transformer.hollowoutput;


public class ArtworkCdn implements Cloneable {

    public int cdnId = java.lang.Integer.MIN_VALUE;
    public Strings cdnDirectory = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ArtworkCdn))
            return false;

        ArtworkCdn o = (ArtworkCdn) other;
        if(o.cdnId != cdnId) return false;
        if(o.cdnDirectory == null) {
            if(cdnDirectory != null) return false;
        } else if(!o.cdnDirectory.equals(cdnDirectory)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + cdnId;
        hashCode = hashCode * 31 + (cdnDirectory == null ? 1237 : cdnDirectory.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ArtworkCdn{");
        builder.append("cdnId=").append(cdnId);
        builder.append(",cdnDirectory=").append(cdnDirectory);
        builder.append("}");
        return builder.toString();
    }

    public ArtworkCdn clone() {
        try {
            ArtworkCdn clone = (ArtworkCdn)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}