package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VideoMetaData implements Cloneable {

    public boolean isTestTitle = false;
    public Set<Strings> titleTypes = null;
    public boolean isSearchOnly = false;
    public boolean isTV = false;
    public boolean hasNewContent = false;
    public int year = java.lang.Integer.MIN_VALUE;
    public int latestYear = java.lang.Integer.MIN_VALUE;
    public boolean isTheatricalRelease = false;
    public Date theatricalReleaseDate = null;
    public List<VPerson> actorList = null;
    public List<VPerson> directorList = null;
    public List<VPerson> creatorList = null;
    public Map<VRole, List<VPerson>> roles = null;
    public int showMemberTypeId = java.lang.Integer.MIN_VALUE;
    public Strings showMemberSequenceLabel = null;
    public Strings copyright = null;
    public Set<VideoSetType> videoSetTypes = null;
    public ISOCountry countryOfOrigin = null;
    public NFLocale countryOfOriginNameLocale = null;
    public Strings originalLanguageBcp47code = null;
    public Set<Strings> aliases = null;
    public Set<Strings> episodeTypes = null;
    public List<Hook> hooks = null;
    public Strings overrideTitle = null;
    public Date broadcastReleaseDate = null;
    public int broadcastReleaseYear = java.lang.Integer.MIN_VALUE;
    public Strings broadcastDistributorName = null;
    public Integer metadataReleaseDays = null;
    public MerchBehavior merchBehavior = null;
    public Integer displayRuntimeInSeconds = null;
    public InteractiveData interactiveData = null;

    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof VideoMetaData))
            return false;

        VideoMetaData o = (VideoMetaData) other;
        if (o.isTestTitle != isTestTitle) return false;
        if (o.titleTypes == null) {
            if (titleTypes != null) return false;
        } else if (!o.titleTypes.equals(titleTypes)) return false;
        if (o.isSearchOnly != isSearchOnly) return false;
        if (o.isTV != isTV) return false;
        if (o.hasNewContent != hasNewContent) return false;
        if (o.year != year) return false;
        if (o.latestYear != latestYear) return false;
        if (o.isTheatricalRelease != isTheatricalRelease) return false;
        if (o.theatricalReleaseDate == null) {
            if (theatricalReleaseDate != null) return false;
        } else if (!o.theatricalReleaseDate.equals(theatricalReleaseDate)) return false;
        if (o.actorList == null) {
            if (actorList != null) return false;
        } else if (!o.actorList.equals(actorList)) return false;
        if (o.directorList == null) {
            if (directorList != null) return false;
        } else if (!o.directorList.equals(directorList)) return false;
        if (o.creatorList == null) {
            if (creatorList != null) return false;
        } else if (!o.creatorList.equals(creatorList)) return false;
        if (o.roles == null) {
            if (roles != null) return false;
        } else if (!o.roles.equals(roles)) return false;
        if (o.showMemberTypeId != showMemberTypeId) return false;
        if (o.showMemberSequenceLabel == null) {
            if (showMemberSequenceLabel != null) return false;
        } else if (!o.showMemberSequenceLabel.equals(showMemberSequenceLabel)) return false;
        if (o.copyright == null) {
            if (copyright != null) return false;
        } else if (!o.copyright.equals(copyright)) return false;
        if (o.videoSetTypes == null) {
            if (videoSetTypes != null) return false;
        } else if (!o.videoSetTypes.equals(videoSetTypes)) return false;
        if (o.countryOfOrigin == null) {
            if (countryOfOrigin != null) return false;
        } else if (!o.countryOfOrigin.equals(countryOfOrigin)) return false;
        if (o.countryOfOriginNameLocale == null) {
            if (countryOfOriginNameLocale != null) return false;
        } else if (!o.countryOfOriginNameLocale.equals(countryOfOriginNameLocale)) return false;
        if (o.originalLanguageBcp47code == null) {
            if (originalLanguageBcp47code != null) return false;
        } else if (!o.originalLanguageBcp47code.equals(originalLanguageBcp47code)) return false;
        if (o.aliases == null) {
            if (aliases != null) return false;
        } else if (!o.aliases.equals(aliases)) return false;
        if (o.episodeTypes == null) {
            if (episodeTypes != null) return false;
        } else if (!o.episodeTypes.equals(episodeTypes)) return false;
        if (o.hooks == null) {
            if (hooks != null) return false;
        } else if (!o.hooks.equals(hooks)) return false;
        if (o.overrideTitle == null) {
            if (overrideTitle != null) return false;
        } else if (!o.overrideTitle.equals(overrideTitle)) return false;
        if (o.broadcastReleaseDate == null) {
            if (broadcastReleaseDate != null) return false;
        } else if (!o.broadcastReleaseDate.equals(broadcastReleaseDate)) return false;
        if (o.broadcastReleaseYear != broadcastReleaseYear) return false;
        if (o.broadcastDistributorName == null) {
            if (broadcastDistributorName != null) return false;
        } else if (!o.broadcastDistributorName.equals(broadcastDistributorName)) return false;
        if (o.metadataReleaseDays == null) {
            if (metadataReleaseDays != null) return false;
        } else if (!o.metadataReleaseDays.equals(metadataReleaseDays)) return false;
        if (o.merchBehavior == null) {
            if (merchBehavior != null) return false;
        } else if (!o.merchBehavior.equals(merchBehavior)) return false;
        if (o.displayRuntimeInSeconds == null) {
            if (displayRuntimeInSeconds != null) return false;
        } else if (!o.displayRuntimeInSeconds.equals(displayRuntimeInSeconds)) return false;
        if (o.interactiveData == null) {
            if (interactiveData != null) return false;
        } else if (!o.interactiveData.equals(interactiveData)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (isTestTitle ? 1231 : 1237);
        hashCode = hashCode * 31 + (titleTypes == null ? 1237 : titleTypes.hashCode());
        hashCode = hashCode * 31 + (isSearchOnly ? 1231 : 1237);
        hashCode = hashCode * 31 + (isTV ? 1231 : 1237);
        hashCode = hashCode * 31 + (hasNewContent ? 1231 : 1237);
        hashCode = hashCode * 31 + year;
        hashCode = hashCode * 31 + latestYear;
        hashCode = hashCode * 31 + (isTheatricalRelease ? 1231 : 1237);
        hashCode = hashCode * 31 + (theatricalReleaseDate == null ? 1237 : theatricalReleaseDate.hashCode());
        hashCode = hashCode * 31 + (actorList == null ? 1237 : actorList.hashCode());
        hashCode = hashCode * 31 + (directorList == null ? 1237 : directorList.hashCode());
        hashCode = hashCode * 31 + (creatorList == null ? 1237 : creatorList.hashCode());
        hashCode = hashCode * 31 + (roles == null ? 1237 : roles.hashCode());
        hashCode = hashCode * 31 + showMemberTypeId;
        hashCode = hashCode * 31 + (showMemberSequenceLabel == null ? 1237 : showMemberSequenceLabel.hashCode());
        hashCode = hashCode * 31 + (copyright == null ? 1237 : copyright.hashCode());
        hashCode = hashCode * 31 + (videoSetTypes == null ? 1237 : videoSetTypes.hashCode());
        hashCode = hashCode * 31 + (countryOfOrigin == null ? 1237 : countryOfOrigin.hashCode());
        hashCode = hashCode * 31 + (countryOfOriginNameLocale == null ? 1237 : countryOfOriginNameLocale.hashCode());
        hashCode = hashCode * 31 + (originalLanguageBcp47code == null ? 1237 : originalLanguageBcp47code.hashCode());
        hashCode = hashCode * 31 + (aliases == null ? 1237 : aliases.hashCode());
        hashCode = hashCode * 31 + (episodeTypes == null ? 1237 : episodeTypes.hashCode());
        hashCode = hashCode * 31 + (hooks == null ? 1237 : hooks.hashCode());
        hashCode = hashCode * 31 + (overrideTitle == null ? 1237 : overrideTitle.hashCode());
        hashCode = hashCode * 31 + (broadcastReleaseDate == null ? 1237 : broadcastReleaseDate.hashCode());
        hashCode = hashCode * 31 + broadcastReleaseYear;
        hashCode = hashCode * 31 + (broadcastDistributorName == null ? 1237 : broadcastDistributorName.hashCode());
        hashCode = hashCode * 31 + (metadataReleaseDays == null ? 1237 : metadataReleaseDays.hashCode());
        hashCode = hashCode * 31 + (merchBehavior == null ? 1237 : merchBehavior.hashCode());
        hashCode = hashCode * 31 + (displayRuntimeInSeconds == null ? 1237 : displayRuntimeInSeconds.hashCode());
        hashCode = hashCode * 31 + (interactiveData == null ? 1237 : interactiveData.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoMetaData{");
        builder.append("isTestTitle=").append(isTestTitle);
        builder.append(",titleTypes=").append(titleTypes);
        builder.append(",isSearchOnly=").append(isSearchOnly);
        builder.append(",isTV=").append(isTV);
        builder.append(",hasNewContent=").append(hasNewContent);
        builder.append(",year=").append(year);
        builder.append(",latestYear=").append(latestYear);
        builder.append(",isTheatricalRelease=").append(isTheatricalRelease);
        builder.append(",theatricalReleaseDate=").append(theatricalReleaseDate);
        builder.append(",actorList=").append(actorList);
        builder.append(",directorList=").append(directorList);
        builder.append(",creatorList=").append(creatorList);
        builder.append(",roles=").append(roles);
        builder.append(",showMemberTypeId=").append(showMemberTypeId);
        builder.append(",showMemberSequenceLabel=").append(showMemberSequenceLabel);
        builder.append(",copyright=").append(copyright);
        builder.append(",videoSetTypes=").append(videoSetTypes);
        builder.append(",countryOfOrigin=").append(countryOfOrigin);
        builder.append(",countryOfOriginNameLocale=").append(countryOfOriginNameLocale);
        builder.append(",originalLanguageBcp47code=").append(originalLanguageBcp47code);
        builder.append(",aliases=").append(aliases);
        builder.append(",episodeTypes=").append(episodeTypes);
        builder.append(",hooks=").append(hooks);
        builder.append(",overrideTitle=").append(overrideTitle);
        builder.append(",broadcastReleaseDate=").append(broadcastReleaseDate);
        builder.append(",broadcastReleaseYear=").append(broadcastReleaseYear);
        builder.append(",broadcastDistributorName=").append(broadcastDistributorName);
        builder.append(",metadataReleaseDays=").append(metadataReleaseDays);
        builder.append(",merchBehavior=").append(merchBehavior);
        builder.append(",displayRuntimeInSeconds=").append(displayRuntimeInSeconds);
        builder.append(",interactiveData=").append(interactiveData);
        builder.append("}");
        return builder.toString();
    }

    public VideoMetaData clone() {
        try {
            VideoMetaData clone = (VideoMetaData) super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    @SuppressWarnings("unused")
    private long __assigned_ordinal = -1;
}
