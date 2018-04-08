package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="DashStreamHeaderData")
public class DashStreamHeaderData implements Cloneable {

    public Set<StreamBoxInfo> boxInfo = null;

    public DashStreamHeaderData() { }

    public DashStreamHeaderData(Set<StreamBoxInfo> value) {
        this.boxInfo = value;
    }

    public DashStreamHeaderData setBoxInfo(Set<StreamBoxInfo> boxInfo) {
        this.boxInfo = boxInfo;
        return this;
    }
    public DashStreamHeaderData addToBoxInfo(StreamBoxInfo streamBoxInfo) {
        if (this.boxInfo == null) {
            this.boxInfo = new HashSet<StreamBoxInfo>();
        }
        this.boxInfo.add(streamBoxInfo);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DashStreamHeaderData))
            return false;

        DashStreamHeaderData o = (DashStreamHeaderData) other;
        if(o.boxInfo == null) {
            if(boxInfo != null) return false;
        } else if(!o.boxInfo.equals(boxInfo)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (boxInfo == null ? 1237 : boxInfo.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DashStreamHeaderData{");
        builder.append("boxInfo=").append(boxInfo);
        builder.append("}");
        return builder.toString();
    }

    public DashStreamHeaderData clone() {
        try {
            DashStreamHeaderData clone = (DashStreamHeaderData)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}