package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="TextStreamInfo")
public class TextStreamInfo implements Cloneable {

    public String textLanguageCode = null;
    public String timedTextType = null;
    public long imageTimedTextMasterIndexOffset = java.lang.Long.MIN_VALUE;
    public long imageTimedTextMasterIndexLength = java.lang.Long.MIN_VALUE;

    public TextStreamInfo setTextLanguageCode(String textLanguageCode) {
        this.textLanguageCode = textLanguageCode;
        return this;
    }
    public TextStreamInfo setTimedTextType(String timedTextType) {
        this.timedTextType = timedTextType;
        return this;
    }
    public TextStreamInfo setImageTimedTextMasterIndexOffset(long imageTimedTextMasterIndexOffset) {
        this.imageTimedTextMasterIndexOffset = imageTimedTextMasterIndexOffset;
        return this;
    }
    public TextStreamInfo setImageTimedTextMasterIndexLength(long imageTimedTextMasterIndexLength) {
        this.imageTimedTextMasterIndexLength = imageTimedTextMasterIndexLength;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof TextStreamInfo))
            return false;

        TextStreamInfo o = (TextStreamInfo) other;
        if(o.textLanguageCode == null) {
            if(textLanguageCode != null) return false;
        } else if(!o.textLanguageCode.equals(textLanguageCode)) return false;
        if(o.timedTextType == null) {
            if(timedTextType != null) return false;
        } else if(!o.timedTextType.equals(timedTextType)) return false;
        if(o.imageTimedTextMasterIndexOffset != imageTimedTextMasterIndexOffset) return false;
        if(o.imageTimedTextMasterIndexLength != imageTimedTextMasterIndexLength) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (textLanguageCode == null ? 1237 : textLanguageCode.hashCode());
        hashCode = hashCode * 31 + (timedTextType == null ? 1237 : timedTextType.hashCode());
        hashCode = hashCode * 31 + (int) (imageTimedTextMasterIndexOffset ^ (imageTimedTextMasterIndexOffset >>> 32));
        hashCode = hashCode * 31 + (int) (imageTimedTextMasterIndexLength ^ (imageTimedTextMasterIndexLength >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("TextStreamInfo{");
        builder.append("textLanguageCode=").append(textLanguageCode);
        builder.append(",timedTextType=").append(timedTextType);
        builder.append(",imageTimedTextMasterIndexOffset=").append(imageTimedTextMasterIndexOffset);
        builder.append(",imageTimedTextMasterIndexLength=").append(imageTimedTextMasterIndexLength);
        builder.append("}");
        return builder.toString();
    }

    public TextStreamInfo clone() {
        try {
            TextStreamInfo clone = (TextStreamInfo)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}