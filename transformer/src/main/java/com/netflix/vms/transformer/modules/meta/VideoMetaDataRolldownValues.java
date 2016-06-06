package com.netflix.vms.transformer.modules.meta;

import com.netflix.vms.transformer.util.RollUpOrDownValues;

public class VideoMetaDataRolldownValues extends RollUpOrDownValues {
    private final int topNodeId;
    private int showMemberTypeId = Integer.MIN_VALUE;

    public VideoMetaDataRolldownValues(int topNodeId) {
        this.topNodeId = topNodeId;
    }

    public int getTopNodeId() {
        return topNodeId;
    }

    public int getShowMemberTypeId() {
        return showMemberTypeId;
    }

    public void setShowMemberTypeId(int showMemberTypeId) {
        this.showMemberTypeId = showMemberTypeId;
    }
}
