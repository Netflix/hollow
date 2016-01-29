package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.Set;

public class PackageData {

    public int id;
    public Video video;
    public Map<ISOCountry, Set<ContractRestriction>> contractRestrictions;
    public Set<StreamData> streams;
    public boolean isPrimaryPackage;
    public Set<EncodeSummaryDescriptor> audioStreamSummary;
    public Set<EncodeSummaryDescriptor> textStreamSummary;
    public Set<EncodeSummaryDescriptor> muxAudioStreamSummary;
    public Set<ISOCountry> allDeployableCountries;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof PackageData))
            return false;

        PackageData o = (PackageData) other;
        if(o.id != id) return false;
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
        return true;
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}