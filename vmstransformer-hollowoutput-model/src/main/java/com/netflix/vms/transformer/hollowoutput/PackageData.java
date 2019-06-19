package com.netflix.vms.transformer.hollowoutput;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

@HollowPrimaryKey(fields="id")
public class PackageData implements Cloneable {

    public int id = java.lang.Integer.MIN_VALUE;
    public Video video = null;
    public Map<ISOCountry, Set<ContractRestriction>> contractRestrictions = null;
    public Set<StreamData> streams = null;
    public boolean isPrimaryPackage = false;
    public boolean isDefaultPackage = true;
    public List<Strings> tags = null;
    public Set<Strings> packageTags = null;
    public Set<EncodeSummaryDescriptor> audioStreamSummary = null;
    public Set<EncodeSummaryDescriptor> textStreamSummary = null;
    public Set<EncodeSummaryDescriptor> muxAudioStreamSummary = null;
    public Set<ISOCountry> allDeployableCountries = null;
    public int runtimeInSeconds = 0;
    public List<TimecodeAnnotation> timecodes = null;
    public List<Strings> recipeGroups = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PackageData))
            return false;

        PackageData o = (PackageData) other;
        if(o.id != id) return false;
        if(o.runtimeInSeconds != runtimeInSeconds) return false;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(o.contractRestrictions == null) {
            if(contractRestrictions != null) return false;
        } else if(!o.contractRestrictions.equals(contractRestrictions)) return false;
        if(o.streams == null) {
            if(streams != null) return false;
        } else if(!o.streams.equals(streams)) return false;
        if(o.isPrimaryPackage != isPrimaryPackage) return false;
        if(o.isDefaultPackage != isDefaultPackage) return false;
        if(o.audioStreamSummary == null) {
            if(audioStreamSummary != null) return false;
        } else if(!o.audioStreamSummary.equals(audioStreamSummary)) return false;
        if(o.textStreamSummary == null) {
            if(textStreamSummary != null) return false;
        } else if(!o.textStreamSummary.equals(textStreamSummary)) return false;
        if(o.muxAudioStreamSummary == null) {
            if(muxAudioStreamSummary != null) return false;
        } else if(!o.muxAudioStreamSummary.equals(muxAudioStreamSummary)) return false;
        if(o.allDeployableCountries == null) {
            if(allDeployableCountries != null) return false;
        } else if(!o.allDeployableCountries.equals(allDeployableCountries)) return false;
        if(o.tags == null) {
            if(tags != null) return false;
        }else if(!o.tags.equals(tags)) return false;
        if(o.packageTags == null) {
            if(packageTags != null) return false;
        }else if(!o.packageTags.equals(packageTags)) return false;
        if(o.timecodes == null) {
        	if(timecodes != null) return false;
        } else if(!o.timecodes.equals(timecodes)) return false;
        if(o.recipeGroups == null) {
            if(recipeGroups != null) return false;
        }else if(!o.recipeGroups.equals(recipeGroups)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + id;
        hashCode = hashCode * 31 + runtimeInSeconds;
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + (contractRestrictions == null ? 1237 : contractRestrictions.hashCode());
        hashCode = hashCode * 31 + (streams == null ? 1237 : streams.hashCode());
        hashCode = hashCode * 31 + (isPrimaryPackage? 1231 : 1237);
        hashCode = hashCode * 31 + (audioStreamSummary == null ? 1237 : audioStreamSummary.hashCode());
        hashCode = hashCode * 31 + (textStreamSummary == null ? 1237 : textStreamSummary.hashCode());
        hashCode = hashCode * 31 + (muxAudioStreamSummary == null ? 1237 : muxAudioStreamSummary.hashCode());
        hashCode = hashCode * 31 + (allDeployableCountries == null ? 1237 : allDeployableCountries.hashCode());
        hashCode = hashCode * 31 + (packageTags == null ? 1237 : packageTags.hashCode());
        hashCode = hashCode * 31 + (timecodes == null ? 1237 : timecodes.hashCode());
        hashCode = hashCode * 31 + (isDefaultPackage ? 1231 : 1237);
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("PackageData{");
        builder.append("id=").append(id);
        builder.append(",video=").append(video);
        builder.append(",runtimeInSeconds=").append(runtimeInSeconds);
        builder.append(",contractRestrictions=").append(contractRestrictions);
        builder.append(",streams=").append(streams);
        builder.append(",isPrimaryPackage=").append(isPrimaryPackage);
        builder.append(",audioStreamSummary=").append(audioStreamSummary);
        builder.append(",textStreamSummary=").append(textStreamSummary);
        builder.append(",muxAudioStreamSummary=").append(muxAudioStreamSummary);
        builder.append(",isDefaultPackage=").append(isDefaultPackage);
        builder.append(",packageTags=").append(packageTags);
        builder.append(",timecodes=").append(timecodes);
        builder.append(",allDeployableCountries=").append(allDeployableCountries);
        builder.append("}");
        return builder.toString();
    }

    public PackageData clone() {
        try {
            PackageData clone = (PackageData)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
