package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="StreamProfileGroups")
public class StreamProfileGroups implements Cloneable {

    public String groupName = null;
    @HollowTypeName(name="StreamProfileIdList")
    public List<StreamProfileId> streamProfileIds = null;

    public StreamProfileGroups setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }
    public StreamProfileGroups setStreamProfileIds(List<StreamProfileId> streamProfileIds) {
        this.streamProfileIds = streamProfileIds;
        return this;
    }
    public StreamProfileGroups addToStreamProfileIds(StreamProfileId streamProfileId) {
        if (this.streamProfileIds == null) {
            this.streamProfileIds = new ArrayList<StreamProfileId>();
        }
        this.streamProfileIds.add(streamProfileId);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamProfileGroups))
            return false;

        StreamProfileGroups o = (StreamProfileGroups) other;
        if(o.groupName == null) {
            if(groupName != null) return false;
        } else if(!o.groupName.equals(groupName)) return false;
        if(o.streamProfileIds == null) {
            if(streamProfileIds != null) return false;
        } else if(!o.streamProfileIds.equals(streamProfileIds)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (groupName == null ? 1237 : groupName.hashCode());
        hashCode = hashCode * 31 + (streamProfileIds == null ? 1237 : streamProfileIds.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamProfileGroups{");
        builder.append("groupName=").append(groupName);
        builder.append(",streamProfileIds=").append(streamProfileIds);
        builder.append("}");
        return builder.toString();
    }

    public StreamProfileGroups clone() {
        try {
            StreamProfileGroups clone = (StreamProfileGroups)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}