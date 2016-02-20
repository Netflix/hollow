package com.netflix.vms.transformer.hollowoutput;


public class ICSMReview {

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

    @SuppressWarnings("unused")
    private int __assigned_ordinal = -1;
}