package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class CSMReviewHollow extends HollowObject {

    public CSMReviewHollow(CSMReviewDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public StringHollow _getStudio() {
        int refOrdinal = delegate().getStudioOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getSexualContentAlert() {
        return delegate().getSexualContentAlert(ordinal);
    }

    public Long _getSexualContentAlertBoxed() {
        return delegate().getSexualContentAlertBoxed(ordinal);
    }

    public long _getConsumerismAlert() {
        return delegate().getConsumerismAlert(ordinal);
    }

    public Long _getConsumerismAlertBoxed() {
        return delegate().getConsumerismAlertBoxed(ordinal);
    }

    public StringHollow _getLink() {
        int refOrdinal = delegate().getLinkOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getOtherChoices() {
        int refOrdinal = delegate().getOtherChoicesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public DateHollow _getPlasticReleaseDate() {
        int refOrdinal = delegate().getPlasticReleaseDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public StringHollow _getAgeExplanation() {
        int refOrdinal = delegate().getAgeExplanationOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSexualContent() {
        int refOrdinal = delegate().getSexualContentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getTitle() {
        int refOrdinal = delegate().getTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getAgeRecommendation() {
        return delegate().getAgeRecommendation(ordinal);
    }

    public Long _getAgeRecommendationBoxed() {
        return delegate().getAgeRecommendationBoxed(ordinal);
    }

    public long _getViolenceAlert() {
        return delegate().getViolenceAlert(ordinal);
    }

    public Long _getViolenceAlertBoxed() {
        return delegate().getViolenceAlertBoxed(ordinal);
    }

    public StringHollow _getReviewerName() {
        int refOrdinal = delegate().getReviewerNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSocialBehavior() {
        int refOrdinal = delegate().getSocialBehaviorOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getParentsNeedToKnow() {
        int refOrdinal = delegate().getParentsNeedToKnowOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getDat() {
        int refOrdinal = delegate().getDatOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getIsItAnyGood() {
        int refOrdinal = delegate().getIsItAnyGoodOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getGenre() {
        int refOrdinal = delegate().getGenreOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getSocialBehaviorAlert() {
        return delegate().getSocialBehaviorAlert(ordinal);
    }

    public Long _getSocialBehaviorAlertBoxed() {
        return delegate().getSocialBehaviorAlertBoxed(ordinal);
    }

    public StringHollow _getConsumerism() {
        int refOrdinal = delegate().getConsumerismOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getWhatsTheStory() {
        int refOrdinal = delegate().getWhatsTheStoryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCastMemberNames() {
        int refOrdinal = delegate().getCastMemberNamesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getViolenceNote() {
        int refOrdinal = delegate().getViolenceNoteOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getLanguageNote() {
        int refOrdinal = delegate().getLanguageNoteOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public DateHollow _getReleaseDate() {
        int refOrdinal = delegate().getReleaseDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public long _getLanguageAlert() {
        return delegate().getLanguageAlert(ordinal);
    }

    public Long _getLanguageAlertBoxed() {
        return delegate().getLanguageAlertBoxed(ordinal);
    }

    public StringHollow _getMediaType() {
        int refOrdinal = delegate().getMediaTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getStars() {
        return delegate().getStars(ordinal);
    }

    public Long _getStarsBoxed() {
        return delegate().getStarsBoxed(ordinal);
    }

    public StringHollow _getDirectorNames() {
        int refOrdinal = delegate().getDirectorNamesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getDatAlert() {
        return delegate().getDatAlert(ordinal);
    }

    public Long _getDatAlertBoxed() {
        return delegate().getDatAlertBoxed(ordinal);
    }

    public StringHollow _getOneLiner() {
        int refOrdinal = delegate().getOneLinerOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getGreenBeginsAge() {
        return delegate().getGreenBeginsAge(ordinal);
    }

    public Long _getGreenBeginsAgeBoxed() {
        return delegate().getGreenBeginsAgeBoxed(ordinal);
    }

    public long _getRedEndsAge() {
        return delegate().getRedEndsAge(ordinal);
    }

    public Long _getRedEndsAgeBoxed() {
        return delegate().getRedEndsAgeBoxed(ordinal);
    }

    public long _getMessageAlert() {
        return delegate().getMessageAlert(ordinal);
    }

    public Long _getMessageAlertBoxed() {
        return delegate().getMessageAlertBoxed(ordinal);
    }

    public StringHollow _getMpaaRating() {
        int refOrdinal = delegate().getMpaaRatingOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getMpaaExplanation() {
        int refOrdinal = delegate().getMpaaExplanationOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getRuntimeInMins() {
        return delegate().getRuntimeInMins(ordinal);
    }

    public Long _getRuntimeInMinsBoxed() {
        return delegate().getRuntimeInMinsBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CSMReviewTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CSMReviewDelegate delegate() {
        return (CSMReviewDelegate)delegate;
    }

}