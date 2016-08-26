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
    public PassthroughVideo source_movie_id = null;
    public ArtworkSourcePassthrough source = null;
    public ArtworkBasicPassthrough basic_passthrough = null;
    public int file_seq = java.lang.Integer.MIN_VALUE;

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
        builder.append(",source_movie_id=").append(source_movie_id);
        builder.append(",source=").append(source);
        builder.append(",basic_passthrough=").append(basic_passthrough);
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