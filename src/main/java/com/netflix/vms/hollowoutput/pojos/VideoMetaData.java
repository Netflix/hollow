package com.netflix.vms.hollowoutput.pojos;

import java.util.Set;
import java.util.List;

public class VideoMetaData {

    public boolean isTestTitle;
    public Set<Strings> titleTypes;
    public boolean isSearchOnly;
    public boolean isTV;
    public boolean hasNewContent;
    public int year;
    public int latestYear;
    public boolean isTheatricalRelease;
    public Date theatricalReleaseDate;
    public List<VPerson> actorList;
    public List<VPerson> directorList;
    public List<VPerson> creatorList;
    public int showMemberTypeId;
    public Strings showMemberSequenceLabel;
    public Strings copyright;
    public Set<VideoSetType> videoSetTypes;
    public ISOCountry countryOfOrigin;
    public NFLocale countryOfOriginNameLocale;
    public Strings originalLanguageBcp47code;
    public Set<Strings> aliases;
    public Set<Strings> episodeTypes;
    public List<Hook> hooks;
    public Strings overrideTitle;

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}