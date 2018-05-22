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
    public Set<ArtworkSourceString> sourceFileIds = null;
    public char[] phaseType = null;
    public boolean isOnHold = false;
    public Video seasonVideo = null;

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
        if(o.sourceFileIds == null) {
            if(sourceFileIds != null) return false;
        } else if(!o.sourceFileIds.equals(sourceFileIds)) return false;
        if(!Arrays.equals(o.phaseType, phaseType)) return false;
        if(o.isOnHold != isOnHold) return false;
        if(o.seasonVideo == null) {
            if(seasonVideo != null) return false;
        } else if(!o.seasonVideo.equals(seasonVideo)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + rolloutId;
        hashCode = hashCode * 31 + (video == null ? 1237 : video.hashCode());
        hashCode = hashCode * 31 + Arrays.hashCode(name);
        hashCode = hashCode * 31 + (isCoreMetaDataShown? 1231 : 1237);
        hashCode = hashCode * 31 + (projectedLaunchDates == null ? 1237 : projectedLaunchDates.hashCode());
        hashCode = hashCode * 31 + (windowsMap == null ? 1237 : windowsMap.hashCode());
        hashCode = hashCode * 31 + (rawL10nAttribs == null ? 1237 : rawL10nAttribs.hashCode());
        hashCode = hashCode * 31 + (trailers == null ? 1237 : trailers.hashCode());
        hashCode = hashCode * 31 + (casts == null ? 1237 : casts.hashCode());
        hashCode = hashCode * 31 + (roles == null ? 1237 : roles.hashCode());
        hashCode = hashCode * 31 + (supplementalVideos == null ? 1237 : supplementalVideos.hashCode());
        hashCode = hashCode * 31 + (sourceFileIds == null ? 1237 : sourceFileIds.hashCode());
        hashCode = hashCode * 31 + Arrays.hashCode(phaseType);
        hashCode = hashCode * 31 + (isOnHold? 1231 : 1237);
        hashCode = hashCode * 31 + (seasonVideo == null ? 1237 : seasonVideo.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Phase{");
        builder.append("rolloutId=").append(rolloutId);
        builder.append(",video=").append(video);
        builder.append(",name=").append(name);
        builder.append(",isCoreMetaDataShown=").append(isCoreMetaDataShown);
        builder.append(",projectedLaunchDates=").append(projectedLaunchDates);
        builder.append(",windowsMap=").append(windowsMap);
        builder.append(",rawL10nAttribs=").append(rawL10nAttribs);
        builder.append(",trailers=").append(trailers);
        builder.append(",casts=").append(casts);
        builder.append(",roles=").append(roles);
        builder.append(",supplementalVideos=").append(supplementalVideos);
        builder.append(",sourceFileIds=").append(sourceFileIds);
        builder.append(",phaseType=").append(phaseType);
        builder.append(",isOnHold=").append(isOnHold);
        builder.append(",seasonVideo=").append(seasonVideo);
        builder.append("}");
        return builder.toString();
    }

    public Phase clone() {
        try {
            Phase clone = (Phase)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
