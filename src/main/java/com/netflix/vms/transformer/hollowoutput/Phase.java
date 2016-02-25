package com.netflix.vms.transformer.hollowoutput;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Phase implements Cloneable {

    public int rolloutId = java.lang.Integer.MIN_VALUE;
    public Video video = null;
    public char[] name = null;
    public boolean isCoreMetaDataShown = false;
    public Map<ISOCountry, Date> projectedLaunchDates = null;
    public Map<ISOCountry, AvailabilityWindow> windowsMap = null;
    public Map<Strings, Strings> rawL10nAttribs = null;
    public List<RolloutTrailer> trailers = null;
    public List<RolloutCast> casts = null;
    public List<RolloutRole> roles = null;
    public List<SupplementalVideo> supplementalVideos = null;
    public Set<Long> artWorkImageIds = null;
    public Set<Strings> sourceFileIds = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Phase))
            return false;

        Phase o = (Phase) other;
        if(o.rolloutId != rolloutId) return false;
        if(o.video == null) {
            if(video != null) return false;
        } else if(!o.video.equals(video)) return false;
        if(!Arrays.equals(o.name, name)) return false;
        if(o.isCoreMetaDataShown != isCoreMetaDataShown) return false;
        if(o.projectedLaunchDates == null) {
            if(projectedLaunchDates != null) return false;
        } else if(!o.projectedLaunchDates.equals(projectedLaunchDates)) return false;
        if(o.windowsMap == null) {
            if(windowsMap != null) return false;
        } else if(!o.windowsMap.equals(windowsMap)) return false;
        if(o.rawL10nAttribs == null) {
            if(rawL10nAttribs != null) return false;
        } else if(!o.rawL10nAttribs.equals(rawL10nAttribs)) return false;
        if(o.trailers == null) {
            if(trailers != null) return false;
        } else if(!o.trailers.equals(trailers)) return false;
        if(o.casts == null) {
            if(casts != null) return false;
        } else if(!o.casts.equals(casts)) return false;
        if(o.roles == null) {
            if(roles != null) return false;
        } else if(!o.roles.equals(roles)) return false;
        if(o.supplementalVideos == null) {
            if(supplementalVideos != null) return false;
        } else if(!o.supplementalVideos.equals(supplementalVideos)) return false;
        if(o.artWorkImageIds == null) {
            if(artWorkImageIds != null) return false;
        } else if(!o.artWorkImageIds.equals(artWorkImageIds)) return false;
        if(o.sourceFileIds == null) {
            if(sourceFileIds != null) return false;
        } else if(!o.sourceFileIds.equals(sourceFileIds)) return false;
        return true;
    }

    public Phase clone() {
        try {
            Phase clone = (Phase)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}