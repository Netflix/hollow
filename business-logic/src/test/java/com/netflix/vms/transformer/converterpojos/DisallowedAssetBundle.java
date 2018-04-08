package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("all")
@HollowTypeName(name="DisallowedAssetBundle")
public class DisallowedAssetBundle implements Cloneable {

    public boolean forceSubtitle = false;
    public String audioLanguageCode = null;
    @HollowTypeName(name="DisallowedSubtitleLangCodesList")
    public List<DisallowedSubtitleLangCode> disallowedSubtitleLangCodes = null;

    public DisallowedAssetBundle setForceSubtitle(boolean forceSubtitle) {
        this.forceSubtitle = forceSubtitle;
        return this;
    }
    public DisallowedAssetBundle setAudioLanguageCode(String audioLanguageCode) {
        this.audioLanguageCode = audioLanguageCode;
        return this;
    }
    public DisallowedAssetBundle setDisallowedSubtitleLangCodes(List<DisallowedSubtitleLangCode> disallowedSubtitleLangCodes) {
        this.disallowedSubtitleLangCodes = disallowedSubtitleLangCodes;
        return this;
    }
    public DisallowedAssetBundle addToDisallowedSubtitleLangCodes(DisallowedSubtitleLangCode disallowedSubtitleLangCode) {
        if (this.disallowedSubtitleLangCodes == null) {
            this.disallowedSubtitleLangCodes = new ArrayList<DisallowedSubtitleLangCode>();
        }
        this.disallowedSubtitleLangCodes.add(disallowedSubtitleLangCode);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof DisallowedAssetBundle))
            return false;

        DisallowedAssetBundle o = (DisallowedAssetBundle) other;
        if(o.forceSubtitle != forceSubtitle) return false;
        if(o.audioLanguageCode == null) {
            if(audioLanguageCode != null) return false;
        } else if(!o.audioLanguageCode.equals(audioLanguageCode)) return false;
        if(o.disallowedSubtitleLangCodes == null) {
            if(disallowedSubtitleLangCodes != null) return false;
        } else if(!o.disallowedSubtitleLangCodes.equals(disallowedSubtitleLangCodes)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (forceSubtitle? 1231 : 1237);
        hashCode = hashCode * 31 + (audioLanguageCode == null ? 1237 : audioLanguageCode.hashCode());
        hashCode = hashCode * 31 + (disallowedSubtitleLangCodes == null ? 1237 : disallowedSubtitleLangCodes.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("DisallowedAssetBundle{");
        builder.append("forceSubtitle=").append(forceSubtitle);
        builder.append(",audioLanguageCode=").append(audioLanguageCode);
        builder.append(",disallowedSubtitleLangCodes=").append(disallowedSubtitleLangCodes);
        builder.append("}");
        return builder.toString();
    }

    public DisallowedAssetBundle clone() {
        try {
            DisallowedAssetBundle clone = (DisallowedAssetBundle)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}