package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class Artwork implements Cloneable {

    public Strings sourceFileId = null;
    public long effectiveDate = java.lang.Long.MIN_VALUE;
    public NFLocale locale = null;
    public int seqNum = java.lang.Integer.MIN_VALUE;
    public int ordinalPriority = java.lang.Integer.MIN_VALUE;
    public ArtworkDerivatives derivatives = null;
    public List<ArtworkCdn> cdns = null;
    public List<DeprecatedImageId> deprecatedImageIds = null;
    public ArtworkBasicPassthrough basic_passthrough = null;
    public ArtworkSourcePassthrough source = null;
    public int file_seq = java.lang.Integer.MIN_VALUE;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Artwork))
            return false;

        Artwork o = (Artwork) other;
        if(o.sourceFileId == null) {
            if(sourceFileId != null) return false;
        } else if(!o.sourceFileId.equals(sourceFileId)) return false;
        if(o.effectiveDate != effectiveDate) return false;
        if(o.locale == null) {
            if(locale != null) return false;
        } else if(!o.locale.equals(locale)) return false;
        if(o.seqNum != seqNum) return false;
        if(o.ordinalPriority != ordinalPriority) return false;
        if(o.derivatives == null) {
            if(derivatives != null) return false;
        } else if(!o.derivatives.equals(derivatives)) return false;
        if(o.cdns == null) {
            if(cdns != null) return false;
        } else if(!o.cdns.equals(cdns)) return false;
        if(o.deprecatedImageIds == null) {
            if(deprecatedImageIds != null) return false;
        } else if(!o.deprecatedImageIds.equals(deprecatedImageIds)) return false;
        if(o.basic_passthrough == null) {
            if(basic_passthrough != null) return false;
        } else if(!o.basic_passthrough.equals(basic_passthrough)) return false;
        if(o.source == null) {
            if(source != null) return false;
        } else if(!o.source.equals(source)) return false;
        if(o.file_seq != file_seq) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (sourceFileId == null ? 1237 : sourceFileId.hashCode());
        hashCode = hashCode * 31 + (int) (effectiveDate ^ (effectiveDate >>> 32));
        hashCode = hashCode * 31 + (locale == null ? 1237 : locale.hashCode());
        hashCode = hashCode * 31 + seqNum;
        hashCode = hashCode * 31 + ordinalPriority;
        hashCode = hashCode * 31 + (derivatives == null ? 1237 : derivatives.hashCode());
        hashCode = hashCode * 31 + (cdns == null ? 1237 : cdns.hashCode());
        hashCode = hashCode * 31 + (deprecatedImageIds == null ? 1237 : deprecatedImageIds.hashCode());
        hashCode = hashCode * 31 + (basic_passthrough == null ? 1237 : basic_passthrough.hashCode());
        hashCode = hashCode * 31 + (source == null ? 1237 : source.hashCode());
        hashCode = hashCode * 31 + file_seq;
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Artwork{");
        builder.append("sourceFileId=").append(sourceFileId);
        builder.append(",effectiveDate=").append(effectiveDate);
        builder.append(",locale=").append(locale);
        builder.append(",seqNum=").append(seqNum);
        builder.append(",ordinalPriority=").append(ordinalPriority);
        builder.append(",derivatives=").append(derivatives);
        builder.append(",cdns=").append(cdns);
        builder.append(",deprecatedImageIds=").append(deprecatedImageIds);
        builder.append(",basic_passthrough=").append(basic_passthrough);
        builder.append(",source=").append(source);
        builder.append(",file_seq=").append(file_seq);
        builder.append("}");
        return builder.toString();
    }

    public Artwork clone() {
        try {
            Artwork clone = (Artwork)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}