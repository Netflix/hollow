package com.netflix.vms.transformer.hollowoutput;

import java.util.List;
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

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoMetaData))
            return false;

        VideoMetaData o = (VideoMetaData) other;
        if(o.isTestTitle != isTestTitle) return false;
        if(o.titleTypes == null) {
            if(titleTypes != null) return false;
        } else if(!o.titleTypes.equals(titleTypes)) return false;
        if(o.isSearchOnly != isSearchOnly) return false;
        if(o.isTV != isTV) return false;
        if(o.hasNewContent != hasNewContent) return false;
        if(o.year != year) return false;
        if(o.latestYear != latestYear) return false;
        if(o.isTheatricalRelease != isTheatricalRelease) return false;
        if(o.theatricalReleaseDate == null) {
            if(theatricalReleaseDate != null) return false;
        } else if(!o.theatricalReleaseDate.equals(theatricalReleaseDate)) return false;
        if(o.actorList == null) {
            if(actorList != null) return false;
        } else if(!o.actorList.equals(actorList)) return false;
        if(o.directorList == null) {
            if(directorList != null) return false;
        } else if(!o.directorList.equals(directorList)) return false;
        if(o.creatorList == null) {
            if(creatorList != null) return false;
        } else if(!o.creatorList.equals(creatorList)) return false;
        if(o.showMemberTypeId != showMemberTypeId) return false;
        if(o.showMemberSequenceLabel == null) {
            if(showMemberSequenceLabel != null) return false;
        } else if(!o.showMemberSequenceLabel.equals(showMemberSequenceLabel)) return false;
        if(o.copyright == null) {
            if(copyright != null) return false;
        } else if(!o.copyright.equals(copyright)) return false;
        if(o.videoSetTypes == null) {
            if(videoSetTypes != null) return false;
        } else if(!o.videoSetTypes.equals(videoSetTypes)) return false;
        if(o.countryOfOrigin == null) {
            if(countryOfOrigin != null) return false;
        } else if(!o.countryOfOrigin.equals(countryOfOrigin)) return false;
        if(o.countryOfOriginNameLocale == null) {
            if(countryOfOriginNameLocale != null) return false;
        } else if(!o.countryOfOriginNameLocale.equals(countryOfOriginNameLocale)) return false;
        if(o.originalLanguageBcp47code == null) {
            if(originalLanguageBcp47code != null) return false;
        } else if(!o.originalLanguageBcp47code.equals(originalLanguageBcp47code)) return false;
        if(o.aliases == null) {
            if(aliases != null) return false;
        } else if(!o.aliases.equals(aliases)) return false;
        if(o.episodeTypes == null) {
            if(episodeTypes != null) return false;
        } else if(!o.episodeTypes.equals(episodeTypes)) return false;
        if(o.hooks == null) {
            if(hooks != null) return false;
        } else if(!o.hooks.equals(hooks)) return false;
        if(o.overrideTitle == null) {
            if(overrideTitle != null) return false;
        } else if(!o.overrideTitle.equals(overrideTitle)) return false;
        return true;
    }

    public VideoMetaData clone() {
        try {
            return (VideoMetaData)super.clone();
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}