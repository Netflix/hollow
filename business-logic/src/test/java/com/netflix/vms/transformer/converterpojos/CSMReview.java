package com.netflix.vms.transformer.converterpojos;

import com.netflix.hollow.core.write.objectmapper.HollowTypeName;


@SuppressWarnings("all")
@HollowTypeName(name="CSMReview")
public class CSMReview implements Cloneable {

    public long videoId = java.lang.Long.MIN_VALUE;
    public String studio = null;
    public long sexualContentAlert = java.lang.Long.MIN_VALUE;
    public long consumerismAlert = java.lang.Long.MIN_VALUE;
    public String link = null;
    public String otherChoices = null;
    public Date plasticReleaseDate = null;
    public String ageExplanation = null;
    public String sexualContent = null;
    public String title = null;
    public long ageRecommendation = java.lang.Long.MIN_VALUE;
    public long violenceAlert = java.lang.Long.MIN_VALUE;
    public String reviewerName = null;
    public String socialBehavior = null;
    public String parentsNeedToKnow = null;
    public String dat = null;
    public String isItAnyGood = null;
    public String genre = null;
    public long socialBehaviorAlert = java.lang.Long.MIN_VALUE;
    public String consumerism = null;
    public String whatsTheStory = null;
    public String castMemberNames = null;
    public String violenceNote = null;
    public String languageNote = null;
    public Date releaseDate = null;
    public long languageAlert = java.lang.Long.MIN_VALUE;
    public String mediaType = null;
    public long stars = java.lang.Long.MIN_VALUE;
    public String directorNames = null;
    public long datAlert = java.lang.Long.MIN_VALUE;
    public String oneLiner = null;
    public long greenBeginsAge = java.lang.Long.MIN_VALUE;
    public long redEndsAge = java.lang.Long.MIN_VALUE;
    public long messageAlert = java.lang.Long.MIN_VALUE;
    public String mpaaRating = null;
    public String mpaaExplanation = null;
    public long runtimeInMins = java.lang.Long.MIN_VALUE;

    public CSMReview setVideoId(long videoId) {
        this.videoId = videoId;
        return this;
    }
    public CSMReview setStudio(String studio) {
        this.studio = studio;
        return this;
    }
    public CSMReview setSexualContentAlert(long sexualContentAlert) {
        this.sexualContentAlert = sexualContentAlert;
        return this;
    }
    public CSMReview setConsumerismAlert(long consumerismAlert) {
        this.consumerismAlert = consumerismAlert;
        return this;
    }
    public CSMReview setLink(String link) {
        this.link = link;
        return this;
    }
    public CSMReview setOtherChoices(String otherChoices) {
        this.otherChoices = otherChoices;
        return this;
    }
    public CSMReview setPlasticReleaseDate(Date plasticReleaseDate) {
        this.plasticReleaseDate = plasticReleaseDate;
        return this;
    }
    public CSMReview setAgeExplanation(String ageExplanation) {
        this.ageExplanation = ageExplanation;
        return this;
    }
    public CSMReview setSexualContent(String sexualContent) {
        this.sexualContent = sexualContent;
        return this;
    }
    public CSMReview setTitle(String title) {
        this.title = title;
        return this;
    }
    public CSMReview setAgeRecommendation(long ageRecommendation) {
        this.ageRecommendation = ageRecommendation;
        return this;
    }
    public CSMReview setViolenceAlert(long violenceAlert) {
        this.violenceAlert = violenceAlert;
        return this;
    }
    public CSMReview setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
        return this;
    }
    public CSMReview setSocialBehavior(String socialBehavior) {
        this.socialBehavior = socialBehavior;
        return this;
    }
    public CSMReview setParentsNeedToKnow(String parentsNeedToKnow) {
        this.parentsNeedToKnow = parentsNeedToKnow;
        return this;
    }
    public CSMReview setDat(String dat) {
        this.dat = dat;
        return this;
    }
    public CSMReview setIsItAnyGood(String isItAnyGood) {
        this.isItAnyGood = isItAnyGood;
        return this;
    }
    public CSMReview setGenre(String genre) {
        this.genre = genre;
        return this;
    }
    public CSMReview setSocialBehaviorAlert(long socialBehaviorAlert) {
        this.socialBehaviorAlert = socialBehaviorAlert;
        return this;
    }
    public CSMReview setConsumerism(String consumerism) {
        this.consumerism = consumerism;
        return this;
    }
    public CSMReview setWhatsTheStory(String whatsTheStory) {
        this.whatsTheStory = whatsTheStory;
        return this;
    }
    public CSMReview setCastMemberNames(String castMemberNames) {
        this.castMemberNames = castMemberNames;
        return this;
    }
    public CSMReview setViolenceNote(String violenceNote) {
        this.violenceNote = violenceNote;
        return this;
    }
    public CSMReview setLanguageNote(String languageNote) {
        this.languageNote = languageNote;
        return this;
    }
    public CSMReview setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }
    public CSMReview setLanguageAlert(long languageAlert) {
        this.languageAlert = languageAlert;
        return this;
    }
    public CSMReview setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }
    public CSMReview setStars(long stars) {
        this.stars = stars;
        return this;
    }
    public CSMReview setDirectorNames(String directorNames) {
        this.directorNames = directorNames;
        return this;
    }
    public CSMReview setDatAlert(long datAlert) {
        this.datAlert = datAlert;
        return this;
    }
    public CSMReview setOneLiner(String oneLiner) {
        this.oneLiner = oneLiner;
        return this;
    }
    public CSMReview setGreenBeginsAge(long greenBeginsAge) {
        this.greenBeginsAge = greenBeginsAge;
        return this;
    }
    public CSMReview setRedEndsAge(long redEndsAge) {
        this.redEndsAge = redEndsAge;
        return this;
    }
    public CSMReview setMessageAlert(long messageAlert) {
        this.messageAlert = messageAlert;
        return this;
    }
    public CSMReview setMpaaRating(String mpaaRating) {
        this.mpaaRating = mpaaRating;
        return this;
    }
    public CSMReview setMpaaExplanation(String mpaaExplanation) {
        this.mpaaExplanation = mpaaExplanation;
        return this;
    }
    public CSMReview setRuntimeInMins(long runtimeInMins) {
        this.runtimeInMins = runtimeInMins;
        return this;
    }
    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof CSMReview))
            return false;

        CSMReview o = (CSMReview) other;
        if(o.videoId != videoId) return false;
        if(o.studio == null) {
            if(studio != null) return false;
        } else if(!o.studio.equals(studio)) return false;
        if(o.sexualContentAlert != sexualContentAlert) return false;
        if(o.consumerismAlert != consumerismAlert) return false;
        if(o.link == null) {
            if(link != null) return false;
        } else if(!o.link.equals(link)) return false;
        if(o.otherChoices == null) {
            if(otherChoices != null) return false;
        } else if(!o.otherChoices.equals(otherChoices)) return false;
        if(o.plasticReleaseDate == null) {
            if(plasticReleaseDate != null) return false;
        } else if(!o.plasticReleaseDate.equals(plasticReleaseDate)) return false;
        if(o.ageExplanation == null) {
            if(ageExplanation != null) return false;
        } else if(!o.ageExplanation.equals(ageExplanation)) return false;
        if(o.sexualContent == null) {
            if(sexualContent != null) return false;
        } else if(!o.sexualContent.equals(sexualContent)) return false;
        if(o.title == null) {
            if(title != null) return false;
        } else if(!o.title.equals(title)) return false;
        if(o.ageRecommendation != ageRecommendation) return false;
        if(o.violenceAlert != violenceAlert) return false;
        if(o.reviewerName == null) {
            if(reviewerName != null) return false;
        } else if(!o.reviewerName.equals(reviewerName)) return false;
        if(o.socialBehavior == null) {
            if(socialBehavior != null) return false;
        } else if(!o.socialBehavior.equals(socialBehavior)) return false;
        if(o.parentsNeedToKnow == null) {
            if(parentsNeedToKnow != null) return false;
        } else if(!o.parentsNeedToKnow.equals(parentsNeedToKnow)) return false;
        if(o.dat == null) {
            if(dat != null) return false;
        } else if(!o.dat.equals(dat)) return false;
        if(o.isItAnyGood == null) {
            if(isItAnyGood != null) return false;
        } else if(!o.isItAnyGood.equals(isItAnyGood)) return false;
        if(o.genre == null) {
            if(genre != null) return false;
        } else if(!o.genre.equals(genre)) return false;
        if(o.socialBehaviorAlert != socialBehaviorAlert) return false;
        if(o.consumerism == null) {
            if(consumerism != null) return false;
        } else if(!o.consumerism.equals(consumerism)) return false;
        if(o.whatsTheStory == null) {
            if(whatsTheStory != null) return false;
        } else if(!o.whatsTheStory.equals(whatsTheStory)) return false;
        if(o.castMemberNames == null) {
            if(castMemberNames != null) return false;
        } else if(!o.castMemberNames.equals(castMemberNames)) return false;
        if(o.violenceNote == null) {
            if(violenceNote != null) return false;
        } else if(!o.violenceNote.equals(violenceNote)) return false;
        if(o.languageNote == null) {
            if(languageNote != null) return false;
        } else if(!o.languageNote.equals(languageNote)) return false;
        if(o.releaseDate == null) {
            if(releaseDate != null) return false;
        } else if(!o.releaseDate.equals(releaseDate)) return false;
        if(o.languageAlert != languageAlert) return false;
        if(o.mediaType == null) {
            if(mediaType != null) return false;
        } else if(!o.mediaType.equals(mediaType)) return false;
        if(o.stars != stars) return false;
        if(o.directorNames == null) {
            if(directorNames != null) return false;
        } else if(!o.directorNames.equals(directorNames)) return false;
        if(o.datAlert != datAlert) return false;
        if(o.oneLiner == null) {
            if(oneLiner != null) return false;
        } else if(!o.oneLiner.equals(oneLiner)) return false;
        if(o.greenBeginsAge != greenBeginsAge) return false;
        if(o.redEndsAge != redEndsAge) return false;
        if(o.messageAlert != messageAlert) return false;
        if(o.mpaaRating == null) {
            if(mpaaRating != null) return false;
        } else if(!o.mpaaRating.equals(mpaaRating)) return false;
        if(o.mpaaExplanation == null) {
            if(mpaaExplanation != null) return false;
        } else if(!o.mpaaExplanation.equals(mpaaExplanation)) return false;
        if(o.runtimeInMins != runtimeInMins) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + (int) (videoId ^ (videoId >>> 32));
        hashCode = hashCode * 31 + (studio == null ? 1237 : studio.hashCode());
        hashCode = hashCode * 31 + (int) (sexualContentAlert ^ (sexualContentAlert >>> 32));
        hashCode = hashCode * 31 + (int) (consumerismAlert ^ (consumerismAlert >>> 32));
        hashCode = hashCode * 31 + (link == null ? 1237 : link.hashCode());
        hashCode = hashCode * 31 + (otherChoices == null ? 1237 : otherChoices.hashCode());
        hashCode = hashCode * 31 + (plasticReleaseDate == null ? 1237 : plasticReleaseDate.hashCode());
        hashCode = hashCode * 31 + (ageExplanation == null ? 1237 : ageExplanation.hashCode());
        hashCode = hashCode * 31 + (sexualContent == null ? 1237 : sexualContent.hashCode());
        hashCode = hashCode * 31 + (title == null ? 1237 : title.hashCode());
        hashCode = hashCode * 31 + (int) (ageRecommendation ^ (ageRecommendation >>> 32));
        hashCode = hashCode * 31 + (int) (violenceAlert ^ (violenceAlert >>> 32));
        hashCode = hashCode * 31 + (reviewerName == null ? 1237 : reviewerName.hashCode());
        hashCode = hashCode * 31 + (socialBehavior == null ? 1237 : socialBehavior.hashCode());
        hashCode = hashCode * 31 + (parentsNeedToKnow == null ? 1237 : parentsNeedToKnow.hashCode());
        hashCode = hashCode * 31 + (dat == null ? 1237 : dat.hashCode());
        hashCode = hashCode * 31 + (isItAnyGood == null ? 1237 : isItAnyGood.hashCode());
        hashCode = hashCode * 31 + (genre == null ? 1237 : genre.hashCode());
        hashCode = hashCode * 31 + (int) (socialBehaviorAlert ^ (socialBehaviorAlert >>> 32));
        hashCode = hashCode * 31 + (consumerism == null ? 1237 : consumerism.hashCode());
        hashCode = hashCode * 31 + (whatsTheStory == null ? 1237 : whatsTheStory.hashCode());
        hashCode = hashCode * 31 + (castMemberNames == null ? 1237 : castMemberNames.hashCode());
        hashCode = hashCode * 31 + (violenceNote == null ? 1237 : violenceNote.hashCode());
        hashCode = hashCode * 31 + (languageNote == null ? 1237 : languageNote.hashCode());
        hashCode = hashCode * 31 + (releaseDate == null ? 1237 : releaseDate.hashCode());
        hashCode = hashCode * 31 + (int) (languageAlert ^ (languageAlert >>> 32));
        hashCode = hashCode * 31 + (mediaType == null ? 1237 : mediaType.hashCode());
        hashCode = hashCode * 31 + (int) (stars ^ (stars >>> 32));
        hashCode = hashCode * 31 + (directorNames == null ? 1237 : directorNames.hashCode());
        hashCode = hashCode * 31 + (int) (datAlert ^ (datAlert >>> 32));
        hashCode = hashCode * 31 + (oneLiner == null ? 1237 : oneLiner.hashCode());
        hashCode = hashCode * 31 + (int) (greenBeginsAge ^ (greenBeginsAge >>> 32));
        hashCode = hashCode * 31 + (int) (redEndsAge ^ (redEndsAge >>> 32));
        hashCode = hashCode * 31 + (int) (messageAlert ^ (messageAlert >>> 32));
        hashCode = hashCode * 31 + (mpaaRating == null ? 1237 : mpaaRating.hashCode());
        hashCode = hashCode * 31 + (mpaaExplanation == null ? 1237 : mpaaExplanation.hashCode());
        hashCode = hashCode * 31 + (int) (runtimeInMins ^ (runtimeInMins >>> 32));
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CSMReview{");
        builder.append("videoId=").append(videoId);
        builder.append(",studio=").append(studio);
        builder.append(",sexualContentAlert=").append(sexualContentAlert);
        builder.append(",consumerismAlert=").append(consumerismAlert);
        builder.append(",link=").append(link);
        builder.append(",otherChoices=").append(otherChoices);
        builder.append(",plasticReleaseDate=").append(plasticReleaseDate);
        builder.append(",ageExplanation=").append(ageExplanation);
        builder.append(",sexualContent=").append(sexualContent);
        builder.append(",title=").append(title);
        builder.append(",ageRecommendation=").append(ageRecommendation);
        builder.append(",violenceAlert=").append(violenceAlert);
        builder.append(",reviewerName=").append(reviewerName);
        builder.append(",socialBehavior=").append(socialBehavior);
        builder.append(",parentsNeedToKnow=").append(parentsNeedToKnow);
        builder.append(",dat=").append(dat);
        builder.append(",isItAnyGood=").append(isItAnyGood);
        builder.append(",genre=").append(genre);
        builder.append(",socialBehaviorAlert=").append(socialBehaviorAlert);
        builder.append(",consumerism=").append(consumerism);
        builder.append(",whatsTheStory=").append(whatsTheStory);
        builder.append(",castMemberNames=").append(castMemberNames);
        builder.append(",violenceNote=").append(violenceNote);
        builder.append(",languageNote=").append(languageNote);
        builder.append(",releaseDate=").append(releaseDate);
        builder.append(",languageAlert=").append(languageAlert);
        builder.append(",mediaType=").append(mediaType);
        builder.append(",stars=").append(stars);
        builder.append(",directorNames=").append(directorNames);
        builder.append(",datAlert=").append(datAlert);
        builder.append(",oneLiner=").append(oneLiner);
        builder.append(",greenBeginsAge=").append(greenBeginsAge);
        builder.append(",redEndsAge=").append(redEndsAge);
        builder.append(",messageAlert=").append(messageAlert);
        builder.append(",mpaaRating=").append(mpaaRating);
        builder.append(",mpaaExplanation=").append(mpaaExplanation);
        builder.append(",runtimeInMins=").append(runtimeInMins);
        builder.append("}");
        return builder.toString();
    }

    public CSMReview clone() {
        try {
            CSMReview clone = (CSMReview)super.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

}