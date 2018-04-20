package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="PackageMoment")
public class PackageMoment implements Cloneable {

    public long clipSpecRuntimeMillis = java.lang.Long.MIN_VALUE;
    public long offsetMillis = java.lang.Long.MIN_VALUE;
    @HollowTypeName(name="DownloadableIdList")
    public List<DownloadableId> downloadableIds = null;
    public long bifIndex = java.lang.Long.MIN_VALUE;
    public String momentType = null;
    public long momentSeqNumber = java.lang.Long.MIN_VALUE;
    public String tags = null;

    public PackageMoment setClipSpecRuntimeMillis(long clipSpecRuntimeMillis) {
        this.clipSpecRuntimeMillis = clipSpecRuntimeMillis;
        return this;
    }
    public PackageMoment setOffsetMillis(long offsetMillis) {
        this.offsetMillis = offsetMillis;
        return this;
    }
    public PackageMoment setDownloadableIds(List<DownloadableId> downloadableIds) {
        this.downloadableIds = downloadableIds;
        return this;
    }
    public PackageMoment setBifIndex(long bifIndex) {
        this.bifIndex = bifIndex;
        return this;
    }
    public PackageMoment setMomentType(String momentType) {
        this.momentType = momentType;
        return this;
    }
    public PackageMoment setMomentSeqNumber(long momentSeqNumber) {
        this.momentSeqNumber = momentSeqNumber;
        return this;
    }
    public PackageMoment setTags(String tags) {
        this.tags = tags;
        return this;
    }
    public PackageMoment addToDownloadableIds(DownloadableId downloadableId) {
        if (this.downloadableIds == null) {
            this.downloadableIds = new ArrayList<DownloadableId>();
        }
        this.downloadableIds.add(downloadableId);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PackageMoment))
            return false;

        PackageMoment o = (PackageMoment) other;
        if(o.clipSpecRuntimeMillis != clipSpecRuntimeMillis) return false;
        if(o.offsetMillis != offsetMillis) return false;
        if(o.downloadableIds == null) {
            if(downloadableIds != null) return false;
        } else if(!o.downloadableIds.equals(downloadableIds)) return false;
        if(o.bifIndex != bifIndex) return false;
        if(o.momentType == null) {
            if(momentType != null) return false;
        } else if(!o.momentType.equals(momentType)) return false;
        if(o.momentSeqNumber != momentSeqNumber) return false;
        if(o.tags == null) {
            if(tags != null) return false;
        } else if(!o.tags.equals(tags)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (clipSpecRuntimeMillis ^ (clipSpecRuntimeMillis >>> 32));
        hashCode = hashCode * 31 + (int) (offsetMillis ^ (offsetMillis >>> 32));
        hashCode = hashCode * 31 + (downloadableIds == null ? 1237 : downloadableIds.hashCode());
        hashCode = hashCode * 31 + (int) (bifIndex ^ (bifIndex >>> 32));
        hashCode = hashCode * 31 + (momentType == null ? 1237 : momentType.hashCode());
        hashCode = hashCode * 31 + (int) (momentSeqNumber ^ (momentSeqNumber >>> 32));
        hashCode = hashCode * 31 + (tags == null ? 1237 : tags.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PackageMoment{");
        builder.append("clipSpecRuntimeMillis=").append(clipSpecRuntimeMillis);
        builder.append(",offsetMillis=").append(offsetMillis);
        builder.append(",downloadableIds=").append(downloadableIds);
        builder.append(",bifIndex=").append(bifIndex);
        builder.append(",momentType=").append(momentType);
        builder.append(",momentSeqNumber=").append(momentSeqNumber);
        builder.append(",tags=").append(tags);
        builder.append("}");
        return builder.toString();
    }

    public PackageMoment clone() {
        try {
            PackageMoment clone = (PackageMoment)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}