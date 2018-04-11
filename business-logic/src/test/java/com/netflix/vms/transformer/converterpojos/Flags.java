package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="Flags")
public class Flags implements Cloneable {

    public boolean searchOnly = false;
    public boolean localText = false;
    public boolean languageOverride = false;
    public boolean localAudio = false;
    public boolean goLive = false;
    public boolean autoPlay = false;
    public Date firstDisplayDate = null;
    @HollowTypeName(name="MapOfFlagsFirstDisplayDates")
    public Map<MapKey, Date> firstDisplayDates = null;
    public boolean liveOnSite = false;
    public List<String> offsiteReasons = null;
    public boolean contentApproved = false;
    public boolean allowIncomplete = false;
    public boolean goLivePartialSubDubIgnored = false;
    public String alternateLanguage = null;
    public boolean hasRequiredLanguages = false;
    public boolean hasRequiredStreams = false;
    public boolean releaseAsAvailable = false;
    public String removeAsset = null;
    public boolean removeFromWebsiteOverride = false;
    public Set<String> requiredLangs = null;
    public boolean searchOnlyOverride = false;
    public Set<String> textRequiredLanguages = null;
    public Set<String> audioRequiredLanguages = null;
    public Set<String> localizationRequiredLanguages = null;

    public Flags setSearchOnly(boolean searchOnly) {
        this.searchOnly = searchOnly;
        return this;
    }
    public Flags setLocalText(boolean localText) {
        this.localText = localText;
        return this;
    }
    public Flags setLanguageOverride(boolean languageOverride) {
        this.languageOverride = languageOverride;
        return this;
    }
    public Flags setLocalAudio(boolean localAudio) {
        this.localAudio = localAudio;
        return this;
    }
    public Flags setGoLive(boolean goLive) {
        this.goLive = goLive;
        return this;
    }
    public Flags setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        return this;
    }
    public Flags setFirstDisplayDate(Date firstDisplayDate) {
        this.firstDisplayDate = firstDisplayDate;
        return this;
    }
    public Flags setFirstDisplayDates(Map<MapKey, Date> firstDisplayDates) {
        this.firstDisplayDates = firstDisplayDates;
        return this;
    }
    public Flags setLiveOnSite(boolean liveOnSite) {
        this.liveOnSite = liveOnSite;
        return this;
    }
    public Flags setOffsiteReasons(List<String> offsiteReasons) {
        this.offsiteReasons = offsiteReasons;
        return this;
    }
    public Flags setContentApproved(boolean contentApproved) {
        this.contentApproved = contentApproved;
        return this;
    }
    public Flags setAllowIncomplete(boolean allowIncomplete) {
        this.allowIncomplete = allowIncomplete;
        return this;
    }
    public Flags setGoLivePartialSubDubIgnored(boolean goLivePartialSubDubIgnored) {
        this.goLivePartialSubDubIgnored = goLivePartialSubDubIgnored;
        return this;
    }
    public Flags setAlternateLanguage(String alternateLanguage) {
        this.alternateLanguage = alternateLanguage;
        return this;
    }
    public Flags setHasRequiredLanguages(boolean hasRequiredLanguages) {
        this.hasRequiredLanguages = hasRequiredLanguages;
        return this;
    }
    public Flags setHasRequiredStreams(boolean hasRequiredStreams) {
        this.hasRequiredStreams = hasRequiredStreams;
        return this;
    }
    public Flags setReleaseAsAvailable(boolean releaseAsAvailable) {
        this.releaseAsAvailable = releaseAsAvailable;
        return this;
    }
    public Flags setRemoveAsset(String removeAsset) {
        this.removeAsset = removeAsset;
        return this;
    }
    public Flags setRemoveFromWebsiteOverride(boolean removeFromWebsiteOverride) {
        this.removeFromWebsiteOverride = removeFromWebsiteOverride;
        return this;
    }
    public Flags setRequiredLangs(Set<String> requiredLangs) {
        this.requiredLangs = requiredLangs;
        return this;
    }
    public Flags setSearchOnlyOverride(boolean searchOnlyOverride) {
        this.searchOnlyOverride = searchOnlyOverride;
        return this;
    }
    public Flags setTextRequiredLanguages(Set<String> textRequiredLanguages) {
        this.textRequiredLanguages = textRequiredLanguages;
        return this;
    }
    public Flags setAudioRequiredLanguages(Set<String> audioRequiredLanguages) {
        this.audioRequiredLanguages = audioRequiredLanguages;
        return this;
    }
    public Flags setLocalizationRequiredLanguages(Set<String> localizationRequiredLanguages) {
        this.localizationRequiredLanguages = localizationRequiredLanguages;
        return this;
    }
    public Flags addToOffsiteReasons(String string) {
        if (this.offsiteReasons == null) {
            this.offsiteReasons = new ArrayList<String>();
        }
        this.offsiteReasons.add(string);
        return this;
    }
    public Flags addToRequiredLangs(String string) {
        if (this.requiredLangs == null) {
            this.requiredLangs = new HashSet<String>();
        }
        this.requiredLangs.add(string);
        return this;
    }
    public Flags addToTextRequiredLanguages(String string) {
        if (this.textRequiredLanguages == null) {
            this.textRequiredLanguages = new HashSet<String>();
        }
        this.textRequiredLanguages.add(string);
        return this;
    }
    public Flags addToAudioRequiredLanguages(String string) {
        if (this.audioRequiredLanguages == null) {
            this.audioRequiredLanguages = new HashSet<String>();
        }
        this.audioRequiredLanguages.add(string);
        return this;
    }
    public Flags addToLocalizationRequiredLanguages(String string) {
        if (this.localizationRequiredLanguages == null) {
            this.localizationRequiredLanguages = new HashSet<String>();
        }
        this.localizationRequiredLanguages.add(string);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof Flags))
            return false;

        Flags o = (Flags) other;
        if(o.searchOnly != searchOnly) return false;
        if(o.localText != localText) return false;
        if(o.languageOverride != languageOverride) return false;
        if(o.localAudio != localAudio) return false;
        if(o.goLive != goLive) return false;
        if(o.autoPlay != autoPlay) return false;
        if(o.firstDisplayDate == null) {
            if(firstDisplayDate != null) return false;
        } else if(!o.firstDisplayDate.equals(firstDisplayDate)) return false;
        if(o.firstDisplayDates == null) {
            if(firstDisplayDates != null) return false;
        } else if(!o.firstDisplayDates.equals(firstDisplayDates)) return false;
        if(o.liveOnSite != liveOnSite) return false;
        if(o.offsiteReasons == null) {
            if(offsiteReasons != null) return false;
        } else if(!o.offsiteReasons.equals(offsiteReasons)) return false;
        if(o.contentApproved != contentApproved) return false;
        if(o.allowIncomplete != allowIncomplete) return false;
        if(o.goLivePartialSubDubIgnored != goLivePartialSubDubIgnored) return false;
        if(o.alternateLanguage == null) {
            if(alternateLanguage != null) return false;
        } else if(!o.alternateLanguage.equals(alternateLanguage)) return false;
        if(o.hasRequiredLanguages != hasRequiredLanguages) return false;
        if(o.hasRequiredStreams != hasRequiredStreams) return false;
        if(o.releaseAsAvailable != releaseAsAvailable) return false;
        if(o.removeAsset == null) {
            if(removeAsset != null) return false;
        } else if(!o.removeAsset.equals(removeAsset)) return false;
        if(o.removeFromWebsiteOverride != removeFromWebsiteOverride) return false;
        if(o.requiredLangs == null) {
            if(requiredLangs != null) return false;
        } else if(!o.requiredLangs.equals(requiredLangs)) return false;
        if(o.searchOnlyOverride != searchOnlyOverride) return false;
        if(o.textRequiredLanguages == null) {
            if(textRequiredLanguages != null) return false;
        } else if(!o.textRequiredLanguages.equals(textRequiredLanguages)) return false;
        if(o.audioRequiredLanguages == null) {
            if(audioRequiredLanguages != null) return false;
        } else if(!o.audioRequiredLanguages.equals(audioRequiredLanguages)) return false;
        if(o.localizationRequiredLanguages == null) {
            if(localizationRequiredLanguages != null) return false;
        } else if(!o.localizationRequiredLanguages.equals(localizationRequiredLanguages)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (searchOnly? 1231 : 1237);
        hashCode = hashCode * 31 + (localText? 1231 : 1237);
        hashCode = hashCode * 31 + (languageOverride? 1231 : 1237);
        hashCode = hashCode * 31 + (localAudio? 1231 : 1237);
        hashCode = hashCode * 31 + (goLive? 1231 : 1237);
        hashCode = hashCode * 31 + (autoPlay? 1231 : 1237);
        hashCode = hashCode * 31 + (firstDisplayDate == null ? 1237 : firstDisplayDate.hashCode());
        hashCode = hashCode * 31 + (firstDisplayDates == null ? 1237 : firstDisplayDates.hashCode());
        hashCode = hashCode * 31 + (liveOnSite? 1231 : 1237);
        hashCode = hashCode * 31 + (offsiteReasons == null ? 1237 : offsiteReasons.hashCode());
        hashCode = hashCode * 31 + (contentApproved? 1231 : 1237);
        hashCode = hashCode * 31 + (allowIncomplete? 1231 : 1237);
        hashCode = hashCode * 31 + (goLivePartialSubDubIgnored? 1231 : 1237);
        hashCode = hashCode * 31 + (alternateLanguage == null ? 1237 : alternateLanguage.hashCode());
        hashCode = hashCode * 31 + (hasRequiredLanguages? 1231 : 1237);
        hashCode = hashCode * 31 + (hasRequiredStreams? 1231 : 1237);
        hashCode = hashCode * 31 + (releaseAsAvailable? 1231 : 1237);
        hashCode = hashCode * 31 + (removeAsset == null ? 1237 : removeAsset.hashCode());
        hashCode = hashCode * 31 + (removeFromWebsiteOverride? 1231 : 1237);
        hashCode = hashCode * 31 + (requiredLangs == null ? 1237 : requiredLangs.hashCode());
        hashCode = hashCode * 31 + (searchOnlyOverride? 1231 : 1237);
        hashCode = hashCode * 31 + (textRequiredLanguages == null ? 1237 : textRequiredLanguages.hashCode());
        hashCode = hashCode * 31 + (audioRequiredLanguages == null ? 1237 : audioRequiredLanguages.hashCode());
        hashCode = hashCode * 31 + (localizationRequiredLanguages == null ? 1237 : localizationRequiredLanguages.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("Flags{");
        builder.append("searchOnly=").append(searchOnly);
        builder.append(",localText=").append(localText);
        builder.append(",languageOverride=").append(languageOverride);
        builder.append(",localAudio=").append(localAudio);
        builder.append(",goLive=").append(goLive);
        builder.append(",autoPlay=").append(autoPlay);
        builder.append(",firstDisplayDate=").append(firstDisplayDate);
        builder.append(",firstDisplayDates=").append(firstDisplayDates);
        builder.append(",liveOnSite=").append(liveOnSite);
        builder.append(",offsiteReasons=").append(offsiteReasons);
        builder.append(",contentApproved=").append(contentApproved);
        builder.append(",allowIncomplete=").append(allowIncomplete);
        builder.append(",goLivePartialSubDubIgnored=").append(goLivePartialSubDubIgnored);
        builder.append(",alternateLanguage=").append(alternateLanguage);
        builder.append(",hasRequiredLanguages=").append(hasRequiredLanguages);
        builder.append(",hasRequiredStreams=").append(hasRequiredStreams);
        builder.append(",releaseAsAvailable=").append(releaseAsAvailable);
        builder.append(",removeAsset=").append(removeAsset);
        builder.append(",removeFromWebsiteOverride=").append(removeFromWebsiteOverride);
        builder.append(",requiredLangs=").append(requiredLangs);
        builder.append(",searchOnlyOverride=").append(searchOnlyOverride);
        builder.append(",textRequiredLanguages=").append(textRequiredLanguages);
        builder.append(",audioRequiredLanguages=").append(audioRequiredLanguages);
        builder.append(",localizationRequiredLanguages=").append(localizationRequiredLanguages);
        builder.append("}");
        return builder.toString();
    }

    public Flags clone() {
        try {
            Flags clone = (Flags)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}