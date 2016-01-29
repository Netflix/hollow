package com.netflix.vms.hollowoutput.pojos;

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.List;

public class Phase {

    public int rolloutId;
    public Video video;
    public char[] name;
    public boolean isCoreMetaDataShown;
    public Map<ISOCountry, Date> projectedLaunchDates;
    public Map<ISOCountry, AvailabilityWindow> windowsMap;
    public Map<Strings, Strings> rawL10nAttribs;
    public List<RolloutTrailer> trailers;
    public List<RolloutCast> casts;
    public List<RolloutRole> roles;
    public List<SupplementalVideo> supplementalVideos;
    public Set<Long> artWorkImageIds;
    public Set<Strings> sourceFileIds;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}