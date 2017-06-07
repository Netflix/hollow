package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class EncodeSummaryDescriptor implements Cloneable {

    public boolean fromMuxedOnlyStreams = false;
    public List<DownloadableId> downloadableIds = null;
    public EncodeSummaryDescriptorData descriptorData = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof EncodeSummaryDescriptor))
            return false;

        EncodeSummaryDescriptor o = (EncodeSummaryDescriptor) other;
        if(o.fromMuxedOnlyStreams != fromMuxedOnlyStreams) return false;
        if(o.downloadableIds == null) {
            if(downloadableIds != null) return false;
        } else if(!o.downloadableIds.equals(downloadableIds)) return false;
        if(o.descriptorData == null) {
            if(descriptorData != null) return false;
        } else if(!o.descriptorData.equals(descriptorData)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (fromMuxedOnlyStreams? 1231 : 1237);
        hashCode = hashCode * 31 + (downloadableIds == null ? 1237 : downloadableIds.hashCode());
        hashCode = hashCode * 31 + (descriptorData == null ? 1237 : descriptorData.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("EncodeSummaryDescriptor{");
        builder.append("fromMuxedOnlyStreams=").append(fromMuxedOnlyStreams);
        builder.append(",downloadableIds=").append(downloadableIds);
        builder.append(",descriptorData=").append(descriptorData);
        builder.append("}");
        return builder.toString();
    }

    public EncodeSummaryDescriptor clone() {
        try {
            EncodeSummaryDescriptor clone = (EncodeSummaryDescriptor)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
