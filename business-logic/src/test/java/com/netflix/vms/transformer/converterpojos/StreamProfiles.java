package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="StreamProfiles")
public class StreamProfiles implements Cloneable {

    public long id = java.lang.Long.MIN_VALUE;
    public long drmType = java.lang.Long.MIN_VALUE;
    public String description = null;
    public boolean is3D = false;
    public String name27AndAbove = null;
    public String mimeType = null;
    public long drmKeyGroup = java.lang.Long.MIN_VALUE;
    public String name26AndBelow = null;
    public long audioChannelCount = java.lang.Long.MIN_VALUE;
    public String profileType = null;
    public String fileExtension = null;
    public boolean isAdaptiveSwitching = false;

    public StreamProfiles setId(long id) {
        this.id = id;
        return this;
    }
    public StreamProfiles setDrmType(long drmType) {
        this.drmType = drmType;
        return this;
    }
    public StreamProfiles setDescription(String description) {
        this.description = description;
        return this;
    }
    public StreamProfiles setIs3D(boolean is3D) {
        this.is3D = is3D;
        return this;
    }
    public StreamProfiles setName27AndAbove(String name27AndAbove) {
        this.name27AndAbove = name27AndAbove;
        return this;
    }
    public StreamProfiles setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }
    public StreamProfiles setDrmKeyGroup(long drmKeyGroup) {
        this.drmKeyGroup = drmKeyGroup;
        return this;
    }
    public StreamProfiles setName26AndBelow(String name26AndBelow) {
        this.name26AndBelow = name26AndBelow;
        return this;
    }
    public StreamProfiles setAudioChannelCount(long audioChannelCount) {
        this.audioChannelCount = audioChannelCount;
        return this;
    }
    public StreamProfiles setProfileType(String profileType) {
        this.profileType = profileType;
        return this;
    }
    public StreamProfiles setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
        return this;
    }
    public StreamProfiles setIsAdaptiveSwitching(boolean isAdaptiveSwitching) {
        this.isAdaptiveSwitching = isAdaptiveSwitching;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof StreamProfiles))
            return false;

        StreamProfiles o = (StreamProfiles) other;
        if(o.id != id) return false;
        if(o.drmType != drmType) return false;
        if(o.description == null) {
            if(description != null) return false;
        } else if(!o.description.equals(description)) return false;
        if(o.is3D != is3D) return false;
        if(o.name27AndAbove == null) {
            if(name27AndAbove != null) return false;
        } else if(!o.name27AndAbove.equals(name27AndAbove)) return false;
        if(o.mimeType == null) {
            if(mimeType != null) return false;
        } else if(!o.mimeType.equals(mimeType)) return false;
        if(o.drmKeyGroup != drmKeyGroup) return false;
        if(o.name26AndBelow == null) {
            if(name26AndBelow != null) return false;
        } else if(!o.name26AndBelow.equals(name26AndBelow)) return false;
        if(o.audioChannelCount != audioChannelCount) return false;
        if(o.profileType == null) {
            if(profileType != null) return false;
        } else if(!o.profileType.equals(profileType)) return false;
        if(o.fileExtension == null) {
            if(fileExtension != null) return false;
        } else if(!o.fileExtension.equals(fileExtension)) return false;
        if(o.isAdaptiveSwitching != isAdaptiveSwitching) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (id ^ (id >>> 32));
        hashCode = hashCode * 31 + (int) (drmType ^ (drmType >>> 32));
        hashCode = hashCode * 31 + (description == null ? 1237 : description.hashCode());
        hashCode = hashCode * 31 + (is3D? 1231 : 1237);
        hashCode = hashCode * 31 + (name27AndAbove == null ? 1237 : name27AndAbove.hashCode());
        hashCode = hashCode * 31 + (mimeType == null ? 1237 : mimeType.hashCode());
        hashCode = hashCode * 31 + (int) (drmKeyGroup ^ (drmKeyGroup >>> 32));
        hashCode = hashCode * 31 + (name26AndBelow == null ? 1237 : name26AndBelow.hashCode());
        hashCode = hashCode * 31 + (int) (audioChannelCount ^ (audioChannelCount >>> 32));
        hashCode = hashCode * 31 + (profileType == null ? 1237 : profileType.hashCode());
        hashCode = hashCode * 31 + (fileExtension == null ? 1237 : fileExtension.hashCode());
        hashCode = hashCode * 31 + (isAdaptiveSwitching? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("StreamProfiles{");
        builder.append("id=").append(id);
        builder.append(",drmType=").append(drmType);
        builder.append(",description=").append(description);
        builder.append(",is3D=").append(is3D);
        builder.append(",name27AndAbove=").append(name27AndAbove);
        builder.append(",mimeType=").append(mimeType);
        builder.append(",drmKeyGroup=").append(drmKeyGroup);
        builder.append(",name26AndBelow=").append(name26AndBelow);
        builder.append(",audioChannelCount=").append(audioChannelCount);
        builder.append(",profileType=").append(profileType);
        builder.append(",fileExtension=").append(fileExtension);
        builder.append(",isAdaptiveSwitching=").append(isAdaptiveSwitching);
        builder.append("}");
        return builder.toString();
    }

    public StreamProfiles clone() {
        try {
            StreamProfiles clone = (StreamProfiles)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}