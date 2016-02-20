package com.netflix.vms.transformer.hollowoutput;

import java.util.List;

public class EncodeSummaryDescriptor {

    public boolean fromMuxedOnlyStreams = false;
    public List<Long> downloadableIds = null;
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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}