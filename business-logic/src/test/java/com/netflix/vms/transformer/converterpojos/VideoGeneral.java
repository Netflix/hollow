package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SuppressWarnings("all")
@HollowTypeName(name="VideoGeneral")
public class VideoGeneral implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    public boolean tv = false;
    @HollowTypeName(name="VideoGeneralAliasList")
    public List<VideoGeneralAlias> aliases = null;
    public String videoType = null;
    public long runtime = java.lang.Long.MIN_VALUE;
    public String supplementalSubType = null;
    public long firstReleaseYear = java.lang.Long.MIN_VALUE;
    public boolean testTitle = false;
    public String originalLanguageBcpCode = null;
    public int metadataReleaseDays = java.lang.Integer.MIN_VALUE;
    public String originCountryCode = null;
    public String originalTitle = null;
    @HollowTypeName(name="VideoGeneralTitleTypeList")
    public List<VideoGeneralTitleType> testTitleTypes = null;
    public String originalTitleBcpCode = null;
    public String internalTitle = null;
    @HollowTypeName(name="VideoGeneralEpisodeTypeList")
    public List<VideoGeneralEpisodeType> episodeTypes = null;
    public Set<String> regulatoryAdvisories = null;

    public VideoGeneral setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public VideoGeneral setTv(boolean tv) {
        this.tv = tv;
        return this;
    }
    public VideoGeneral setAliases(List<VideoGeneralAlias> aliases) {
        this.aliases = aliases;
        return this;
    }
    public VideoGeneral setVideoType(String videoType) {
        this.videoType = videoType;
        return this;
    }
    public VideoGeneral setRuntime(long runtime) {
        this.runtime = runtime;
        return this;
    }
    public VideoGeneral setSupplementalSubType(String supplementalSubType) {
        this.supplementalSubType = supplementalSubType;
        return this;
    }
    public VideoGeneral setFirstReleaseYear(long firstReleaseYear) {
        this.firstReleaseYear = firstReleaseYear;
        return this;
    }
    public VideoGeneral setTestTitle(boolean testTitle) {
        this.testTitle = testTitle;
        return this;
    }
    public VideoGeneral setOriginalLanguageBcpCode(String originalLanguageBcpCode) {
        this.originalLanguageBcpCode = originalLanguageBcpCode;
        return this;
    }
    public VideoGeneral setMetadataReleaseDays(int metadataReleaseDays) {
        this.metadataReleaseDays = metadataReleaseDays;
        return this;
    }
    public VideoGeneral setOriginCountryCode(String originCountryCode) {
        this.originCountryCode = originCountryCode;
        return this;
    }
    public VideoGeneral setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
        return this;
    }
    public VideoGeneral setTestTitleTypes(List<VideoGeneralTitleType> testTitleTypes) {
        this.testTitleTypes = testTitleTypes;
        return this;
    }
    public VideoGeneral setOriginalTitleBcpCode(String originalTitleBcpCode) {
        this.originalTitleBcpCode = originalTitleBcpCode;
        return this;
    }
    public VideoGeneral setInternalTitle(String internalTitle) {
        this.internalTitle = internalTitle;
        return this;
    }
    public VideoGeneral setEpisodeTypes(List<VideoGeneralEpisodeType> episodeTypes) {
        this.episodeTypes = episodeTypes;
        return this;
    }
    public VideoGeneral setRegulatoryAdvisories(Set<String> regulatoryAdvisories) {
        this.regulatoryAdvisories = regulatoryAdvisories;
        return this;
    }
    public VideoGeneral addToAliases(VideoGeneralAlias videoGeneralAlias) {
        if (this.aliases == null) {
            this.aliases = new ArrayList<VideoGeneralAlias>();
        }
        this.aliases.add(videoGeneralAlias);
        return this;
    }
    public VideoGeneral addToTestTitleTypes(VideoGeneralTitleType videoGeneralTitleType) {
        if (this.testTitleTypes == null) {
            this.testTitleTypes = new ArrayList<VideoGeneralTitleType>();
        }
        this.testTitleTypes.add(videoGeneralTitleType);
        return this;
    }
    public VideoGeneral addToEpisodeTypes(VideoGeneralEpisodeType videoGeneralEpisodeType) {
        if (this.episodeTypes == null) {
            this.episodeTypes = new ArrayList<VideoGeneralEpisodeType>();
        }
        this.episodeTypes.add(videoGeneralEpisodeType);
        return this;
    }
    public VideoGeneral addToRegulatoryAdvisories(String string) {
        if (this.regulatoryAdvisories == null) {
            this.regulatoryAdvisories = new HashSet<String>();
        }
        this.regulatoryAdvisories.add(string);
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof VideoGeneral))
            return false;

        VideoGeneral o = (VideoGeneral) other;
        if(o.videoId != videoId) return false;
        if(o.tv != tv) return false;
        if(o.aliases == null) {
            if(aliases != null) return false;
        } else if(!o.aliases.equals(aliases)) return false;
        if(o.videoType == null) {
            if(videoType != null) return false;
        } else if(!o.videoType.equals(videoType)) return false;
        if(o.runtime != runtime) return false;
        if(o.supplementalSubType == null) {
            if(supplementalSubType != null) return false;
        } else if(!o.supplementalSubType.equals(supplementalSubType)) return false;
        if(o.firstReleaseYear != firstReleaseYear) return false;
        if(o.testTitle != testTitle) return false;
        if(o.originalLanguageBcpCode == null) {
            if(originalLanguageBcpCode != null) return false;
        } else if(!o.originalLanguageBcpCode.equals(originalLanguageBcpCode)) return false;
        if(o.metadataReleaseDays != metadataReleaseDays) return false;
        if(o.originCountryCode == null) {
            if(originCountryCode != null) return false;
        } else if(!o.originCountryCode.equals(originCountryCode)) return false;
        if(o.originalTitle == null) {
            if(originalTitle != null) return false;
        } else if(!o.originalTitle.equals(originalTitle)) return false;
        if(o.testTitleTypes == null) {
            if(testTitleTypes != null) return false;
        } else if(!o.testTitleTypes.equals(testTitleTypes)) return false;
        if(o.originalTitleBcpCode == null) {
            if(originalTitleBcpCode != null) return false;
        } else if(!o.originalTitleBcpCode.equals(originalTitleBcpCode)) return false;
        if(o.internalTitle == null) {
            if(internalTitle != null) return false;
        } else if(!o.internalTitle.equals(internalTitle)) return false;
        if(o.episodeTypes == null) {
            if(episodeTypes != null) return false;
        } else if(!o.episodeTypes.equals(episodeTypes)) return false;
        if(o.regulatoryAdvisories == null) {
            if(regulatoryAdvisories != null) return false;
        } else if(!o.regulatoryAdvisories.equals(regulatoryAdvisories)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (tv? 1231 : 1237);
        hashCode = hashCode * 31 + (aliases == null ? 1237 : aliases.hashCode());
        hashCode = hashCode * 31 + (videoType == null ? 1237 : videoType.hashCode());
        hashCode = hashCode * 31 + (int) (runtime ^ (runtime >>> 32));
        hashCode = hashCode * 31 + (supplementalSubType == null ? 1237 : supplementalSubType.hashCode());
        hashCode = hashCode * 31 + (int) (firstReleaseYear ^ (firstReleaseYear >>> 32));
        hashCode = hashCode * 31 + (testTitle? 1231 : 1237);
        hashCode = hashCode * 31 + (originalLanguageBcpCode == null ? 1237 : originalLanguageBcpCode.hashCode());
        hashCode = hashCode * 31 + metadataReleaseDays;
        hashCode = hashCode * 31 + (originCountryCode == null ? 1237 : originCountryCode.hashCode());
        hashCode = hashCode * 31 + (originalTitle == null ? 1237 : originalTitle.hashCode());
        hashCode = hashCode * 31 + (testTitleTypes == null ? 1237 : testTitleTypes.hashCode());
        hashCode = hashCode * 31 + (originalTitleBcpCode == null ? 1237 : originalTitleBcpCode.hashCode());
        hashCode = hashCode * 31 + (internalTitle == null ? 1237 : internalTitle.hashCode());
        hashCode = hashCode * 31 + (episodeTypes == null ? 1237 : episodeTypes.hashCode());
        hashCode = hashCode * 31 + (regulatoryAdvisories == null ? 1237 : regulatoryAdvisories.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("VideoGeneral{");
        builder.append("videoId=").append(videoId);
        builder.append(",tv=").append(tv);
        builder.append(",aliases=").append(aliases);
        builder.append(",videoType=").append(videoType);
        builder.append(",runtime=").append(runtime);
        builder.append(",supplementalSubType=").append(supplementalSubType);
        builder.append(",firstReleaseYear=").append(firstReleaseYear);
        builder.append(",testTitle=").append(testTitle);
        builder.append(",originalLanguageBcpCode=").append(originalLanguageBcpCode);
        builder.append(",metadataReleaseDays=").append(metadataReleaseDays);
        builder.append(",originCountryCode=").append(originCountryCode);
        builder.append(",originalTitle=").append(originalTitle);
        builder.append(",testTitleTypes=").append(testTitleTypes);
        builder.append(",originalTitleBcpCode=").append(originalTitleBcpCode);
        builder.append(",internalTitle=").append(internalTitle);
        builder.append(",episodeTypes=").append(episodeTypes);
        builder.append(",regulatoryAdvisories=").append(regulatoryAdvisories);
        builder.append("}");
        return builder.toString();
    }

    public VideoGeneral clone() {
        try {
            VideoGeneral clone = (VideoGeneral)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}