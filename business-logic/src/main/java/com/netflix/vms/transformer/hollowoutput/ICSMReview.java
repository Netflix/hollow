package com.netflix.vms.transformer.hollowoutput;


public class ICSMReview implements Cloneable {

    public int movieID = java.lang.Integer.MIN_VALUE;
    public Strings title = null;
    public Strings link = null;
    public Strings oneLiner = null;
    public Strings reviewerName = null;
    public Strings mediaType = null;
    public int ageRecommendation = java.lang.Integer.MIN_VALUE;
    public Strings ageExplanation = null;
    public int stars = java.lang.Integer.MIN_VALUE;
    public Strings sexualContent = null;
    public int sexualContentAlert = java.lang.Integer.MIN_VALUE;
    public Strings languageNote = null;
    public int languageAlert = java.lang.Integer.MIN_VALUE;
    public Strings violenceNote = null;
    public int violenceAlert = java.lang.Integer.MIN_VALUE;
    public Strings consumerism = null;
    public int consumerismAlert = java.lang.Integer.MIN_VALUE;
    public Strings dat = null;
    public int datAlert = java.lang.Integer.MIN_VALUE;
    public Strings socialBehavior = null;
    public int socialBehaviorAlert = java.lang.Integer.MIN_VALUE;
    public int messageAlert = java.lang.Integer.MIN_VALUE;
    public Strings whatsTheStory = null;
    public Strings isItAnyGood = null;
    public Strings otherChoices = null;
    public Strings parentsNeedToKnow = null;
    public Strings imgSmall = null;
    public Strings imgLarge = null;
    public int redEndsAge = java.lang.Integer.MIN_VALUE;
    public int greenBeginsAge = java.lang.Integer.MIN_VALUE;
    public Date releaseDate = null;
    public Strings mPAARating = null;
    public Strings mPAAExplanation = null;
    public Strings genre = null;
    public Strings directorNames = null;
    public Strings castMemberNames = null;
    public Date plasticReleaseDate = null;
    public int runtimeInMins = java.lang.Integer.MIN_VALUE;
    public Strings studio = null;

    public boolean equals(Object other) {
        if(other == this)  return true;
        if(!(other instanceof ICSMReview))
            return false;

        ICSMReview o = (ICSMReview) other;
        if(o.movieID != movieID) return false;
        if(o.title == null) {
            if(title != null) return false;
        } else if(!o.title.equals(title)) return false;
        if(o.link == null) {
            if(link != null) return false;
        } else if(!o.link.equals(link)) return false;
        if(o.oneLiner == null) {
            if(oneLiner != null) return false;
        } else if(!o.oneLiner.equals(oneLiner)) return false;
        if(o.reviewerName == null) {
            if(reviewerName != null) return false;
        } else if(!o.reviewerName.equals(reviewerName)) return false;
        if(o.mediaType == null) {
            if(mediaType != null) return false;
        } else if(!o.mediaType.equals(mediaType)) return false;
        if(o.ageRecommendation != ageRecommendation) return false;
        if(o.ageExplanation == null) {
            if(ageExplanation != null) return false;
        } else if(!o.ageExplanation.equals(ageExplanation)) return false;
        if(o.stars != stars) return false;
        if(o.sexualContent == null) {
            if(sexualContent != null) return false;
        } else if(!o.sexualContent.equals(sexualContent)) return false;
        if(o.sexualContentAlert != sexualContentAlert) return false;
        if(o.languageNote == null) {
            if(languageNote != null) return false;
        } else if(!o.languageNote.equals(languageNote)) return false;
        if(o.languageAlert != languageAlert) return false;
        if(o.violenceNote == null) {
            if(violenceNote != null) return false;
        } else if(!o.violenceNote.equals(violenceNote)) return false;
        if(o.violenceAlert != violenceAlert) return false;
        if(o.consumerism == null) {
            if(consumerism != null) return false;
        } else if(!o.consumerism.equals(consumerism)) return false;
        if(o.consumerismAlert != consumerismAlert) return false;
        if(o.dat == null) {
            if(dat != null) return false;
        } else if(!o.dat.equals(dat)) return false;
        if(o.datAlert != datAlert) return false;
        if(o.socialBehavior == null) {
            if(socialBehavior != null) return false;
        } else if(!o.socialBehavior.equals(socialBehavior)) return false;
        if(o.socialBehaviorAlert != socialBehaviorAlert) return false;
        if(o.messageAlert != messageAlert) return false;
        if(o.whatsTheStory == null) {
            if(whatsTheStory != null) return false;
        } else if(!o.whatsTheStory.equals(whatsTheStory)) return false;
        if(o.isItAnyGood == null) {
            if(isItAnyGood != null) return false;
        } else if(!o.isItAnyGood.equals(isItAnyGood)) return false;
        if(o.otherChoices == null) {
            if(otherChoices != null) return false;
        } else if(!o.otherChoices.equals(otherChoices)) return false;
        if(o.parentsNeedToKnow == null) {
            if(parentsNeedToKnow != null) return false;
        } else if(!o.parentsNeedToKnow.equals(parentsNeedToKnow)) return false;
        if(o.imgSmall == null) {
            if(imgSmall != null) return false;
        } else if(!o.imgSmall.equals(imgSmall)) return false;
        if(o.imgLarge == null) {
            if(imgLarge != null) return false;
        } else if(!o.imgLarge.equals(imgLarge)) return false;
        if(o.redEndsAge != redEndsAge) return false;
        if(o.greenBeginsAge != greenBeginsAge) return false;
        if(o.releaseDate == null) {
            if(releaseDate != null) return false;
        } else if(!o.releaseDate.equals(releaseDate)) return false;
        if(o.mPAARating == null) {
            if(mPAARating != null) return false;
        } else if(!o.mPAARating.equals(mPAARating)) return false;
        if(o.mPAAExplanation == null) {
            if(mPAAExplanation != null) return false;
        } else if(!o.mPAAExplanation.equals(mPAAExplanation)) return false;
        if(o.genre == null) {
            if(genre != null) return false;
        } else if(!o.genre.equals(genre)) return false;
        if(o.directorNames == null) {
            if(directorNames != null) return false;
        } else if(!o.directorNames.equals(directorNames)) return false;
        if(o.castMemberNames == null) {
            if(castMemberNames != null) return false;
        } else if(!o.castMemberNames.equals(castMemberNames)) return false;
        if(o.plasticReleaseDate == null) {
            if(plasticReleaseDate != null) return false;
        } else if(!o.plasticReleaseDate.equals(plasticReleaseDate)) return false;
        if(o.runtimeInMins != runtimeInMins) return false;
        if(o.studio == null) {
            if(studio != null) return false;
        } else if(!o.studio.equals(studio)) return false;
        return true;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 31 + movieID;
        hashCode = hashCode * 31 + (title == null ? 1237 : title.hashCode());
        hashCode = hashCode * 31 + (link == null ? 1237 : link.hashCode());
        hashCode = hashCode * 31 + (oneLiner == null ? 1237 : oneLiner.hashCode());
        hashCode = hashCode * 31 + (reviewerName == null ? 1237 : reviewerName.hashCode());
        hashCode = hashCode * 31 + (mediaType == null ? 1237 : mediaType.hashCode());
        hashCode = hashCode * 31 + ageRecommendation;
        hashCode = hashCode * 31 + (ageExplanation == null ? 1237 : ageExplanation.hashCode());
        hashCode = hashCode * 31 + stars;
        hashCode = hashCode * 31 + (sexualContent == null ? 1237 : sexualContent.hashCode());
        hashCode = hashCode * 31 + sexualContentAlert;
        hashCode = hashCode * 31 + (languageNote == null ? 1237 : languageNote.hashCode());
        hashCode = hashCode * 31 + languageAlert;
        hashCode = hashCode * 31 + (violenceNote == null ? 1237 : violenceNote.hashCode());
        hashCode = hashCode * 31 + violenceAlert;
        hashCode = hashCode * 31 + (consumerism == null ? 1237 : consumerism.hashCode());
        hashCode = hashCode * 31 + consumerismAlert;
        hashCode = hashCode * 31 + (dat == null ? 1237 : dat.hashCode());
        hashCode = hashCode * 31 + datAlert;
        hashCode = hashCode * 31 + (socialBehavior == null ? 1237 : socialBehavior.hashCode());
        hashCode = hashCode * 31 + socialBehaviorAlert;
        hashCode = hashCode * 31 + messageAlert;
        hashCode = hashCode * 31 + (whatsTheStory == null ? 1237 : whatsTheStory.hashCode());
        hashCode = hashCode * 31 + (isItAnyGood == null ? 1237 : isItAnyGood.hashCode());
        hashCode = hashCode * 31 + (otherChoices == null ? 1237 : otherChoices.hashCode());
        hashCode = hashCode * 31 + (parentsNeedToKnow == null ? 1237 : parentsNeedToKnow.hashCode());
        hashCode = hashCode * 31 + (imgSmall == null ? 1237 : imgSmall.hashCode());
        hashCode = hashCode * 31 + (imgLarge == null ? 1237 : imgLarge.hashCode());
        hashCode = hashCode * 31 + redEndsAge;
        hashCode = hashCode * 31 + greenBeginsAge;
        hashCode = hashCode * 31 + (releaseDate == null ? 1237 : releaseDate.hashCode());
        hashCode = hashCode * 31 + (mPAARating == null ? 1237 : mPAARating.hashCode());
        hashCode = hashCode * 31 + (mPAAExplanation == null ? 1237 : mPAAExplanation.hashCode());
        hashCode = hashCode * 31 + (genre == null ? 1237 : genre.hashCode());
        hashCode = hashCode * 31 + (directorNames == null ? 1237 : directorNames.hashCode());
        hashCode = hashCode * 31 + (castMemberNames == null ? 1237 : castMemberNames.hashCode());
        hashCode = hashCode * 31 + (plasticReleaseDate == null ? 1237 : plasticReleaseDate.hashCode());
        hashCode = hashCode * 31 + runtimeInMins;
        hashCode = hashCode * 31 + (studio == null ? 1237 : studio.hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("ICSMReview{");
        builder.append("movieID=").append(movieID);
        builder.append(",title=").append(title);
        builder.append(",link=").append(link);
        builder.append(",oneLiner=").append(oneLiner);
        builder.append(",reviewerName=").append(reviewerName);
        builder.append(",mediaType=").append(mediaType);
        builder.append(",ageRecommendation=").append(ageRecommendation);
        builder.append(",ageExplanation=").append(ageExplanation);
        builder.append(",stars=").append(stars);
        builder.append(",sexualContent=").append(sexualContent);
        builder.append(",sexualContentAlert=").append(sexualContentAlert);
        builder.append(",languageNote=").append(languageNote);
        builder.append(",languageAlert=").append(languageAlert);
        builder.append(",violenceNote=").append(violenceNote);
        builder.append(",violenceAlert=").append(violenceAlert);
        builder.append(",consumerism=").append(consumerism);
        builder.append(",consumerismAlert=").append(consumerismAlert);
        builder.append(",dat=").append(dat);
        builder.append(",datAlert=").append(datAlert);
        builder.append(",socialBehavior=").append(socialBehavior);
        builder.append(",socialBehaviorAlert=").append(socialBehaviorAlert);
        builder.append(",messageAlert=").append(messageAlert);
        builder.append(",whatsTheStory=").append(whatsTheStory);
        builder.append(",isItAnyGood=").append(isItAnyGood);
        builder.append(",otherChoices=").append(otherChoices);
        builder.append(",parentsNeedToKnow=").append(parentsNeedToKnow);
        builder.append(",imgSmall=").append(imgSmall);
        builder.append(",imgLarge=").append(imgLarge);
        builder.append(",redEndsAge=").append(redEndsAge);
        builder.append(",greenBeginsAge=").append(greenBeginsAge);
        builder.append(",releaseDate=").append(releaseDate);
        builder.append(",mPAARating=").append(mPAARating);
        builder.append(",mPAAExplanation=").append(mPAAExplanation);
        builder.append(",genre=").append(genre);
        builder.append(",directorNames=").append(directorNames);
        builder.append(",castMemberNames=").append(castMemberNames);
        builder.append(",plasticReleaseDate=").append(plasticReleaseDate);
        builder.append(",runtimeInMins=").append(runtimeInMins);
        builder.append(",studio=").append(studio);
        builder.append("}");
        return builder.toString();
    }

    public ICSMReview clone() {
        try {
            ICSMReview clone = (ICSMReview)super.clone();
            clone.__assigned_ordinal = -1;
            return clone;
        } catch (CloneNotSupportedException cnse) { throw new RuntimeException(cnse); }
    }

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}