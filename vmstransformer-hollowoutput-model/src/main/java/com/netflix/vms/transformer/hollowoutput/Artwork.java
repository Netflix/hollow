package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowShardLargeType;

@HollowShardLargeType(numShards=32)
public class Artwork implements Cloneable {

    public ArtworkSourceString sourceFileId = null;
    public long effectiveDate = java.lang.Long.MIN_VALUE;
    public NFLocale locale = null;
    public int seqNum = java.lang.Integer.MIN_VALUE;
    public int ordinalPriority = java.lang.Integer.MIN_VALUE;
    public ArtworkDerivatives derivatives = null;
    public PassthroughVideo source_movie_id = null;
    public ArtworkSourcePassthrough source = null;
    public ArtworkBasicPassthrough basic_passthrough = null;
    public ArtworkMerchStillPackageData merchstillsPackageData = null;
    public int file_seq = java.lang.Integer.MIN_VALUE;
    public AcquisitionSource acquisitionSource = null;
    public boolean isRolloutExclusive = false;
    public SchedulePhaseInfo schedulePhaseInfo = null;
    public int sourceVideoId = java.lang.Integer.MIN_VALUE;
    public boolean hasShowLevelTag = false;

    public String toString() {
        StringBuilder builder = new StringBuilder("Artwork{");
        builder.append("sourceFileId=").append(sourceFileId);
        builder.append(",effectiveDate=").append(effectiveDate);
        builder.append(",locale=").append(locale);
        builder.append(",seqNum=").append(seqNum);
        builder.append(",ordinalPriority=").append(ordinalPriority);
        builder.append(",derivatives=").append(derivatives);
        builder.append(",source_movie_id=").append(source_movie_id);
        builder.append(",source=").append(source);
        builder.append(",basic_passthrough=").append(basic_passthrough);
        builder.append(",file_seq=").append(file_seq);
        builder.append(",merchstillsPackage=").append(merchstillsPackageData);
        builder.append(",acquisitionSource=").append(acquisitionSource);
        builder.append(",availabilityWindows=").append(schedulePhaseInfo);
        builder.append(",isRolloutExclusive=").append(isRolloutExclusive);
        builder.append(",sourceVideoId=").append(sourceVideoId);
        builder.append(",hasShowLevelTag=").append(hasShowLevelTag);
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
    private long __assigned_ordinal = -1;
}
