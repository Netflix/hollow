package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface CSMReviewDelegate extends HollowObjectDelegate {

    public long getVideoId(int ordinal);

    public Long getVideoIdBoxed(int ordinal);

    public int getStudioOrdinal(int ordinal);

    public long getSexualContentAlert(int ordinal);

    public Long getSexualContentAlertBoxed(int ordinal);

    public long getConsumerismAlert(int ordinal);

    public Long getConsumerismAlertBoxed(int ordinal);

    public int getLinkOrdinal(int ordinal);

    public int getOtherChoicesOrdinal(int ordinal);

    public int getPlasticReleaseDateOrdinal(int ordinal);

    public int getAgeExplanationOrdinal(int ordinal);

    public int getSexualContentOrdinal(int ordinal);

    public int getTitleOrdinal(int ordinal);

    public long getAgeRecommendation(int ordinal);

    public Long getAgeRecommendationBoxed(int ordinal);

    public long getViolenceAlert(int ordinal);

    public Long getViolenceAlertBoxed(int ordinal);

    public int getReviewerNameOrdinal(int ordinal);

    public int getSocialBehaviorOrdinal(int ordinal);

    public int getParentsNeedToKnowOrdinal(int ordinal);

    public int getDatOrdinal(int ordinal);

    public int getIsItAnyGoodOrdinal(int ordinal);

    public int getGenreOrdinal(int ordinal);

    public long getSocialBehaviorAlert(int ordinal);

    public Long getSocialBehaviorAlertBoxed(int ordinal);

    public int getConsumerismOrdinal(int ordinal);

    public int getWhatsTheStoryOrdinal(int ordinal);

    public int getCastMemberNamesOrdinal(int ordinal);

    public int getViolenceNoteOrdinal(int ordinal);

    public int getLanguageNoteOrdinal(int ordinal);

    public int getReleaseDateOrdinal(int ordinal);

    public long getLanguageAlert(int ordinal);

    public Long getLanguageAlertBoxed(int ordinal);

    public int getMediaTypeOrdinal(int ordinal);

    public long getStars(int ordinal);

    public Long getStarsBoxed(int ordinal);

    public int getDirectorNamesOrdinal(int ordinal);

    public long getDatAlert(int ordinal);

    public Long getDatAlertBoxed(int ordinal);

    public int getOneLinerOrdinal(int ordinal);

    public long getGreenBeginsAge(int ordinal);

    public Long getGreenBeginsAgeBoxed(int ordinal);

    public long getRedEndsAge(int ordinal);

    public Long getRedEndsAgeBoxed(int ordinal);

    public long getMessageAlert(int ordinal);

    public Long getMessageAlertBoxed(int ordinal);

    public int getMpaaRatingOrdinal(int ordinal);

    public int getMpaaExplanationOrdinal(int ordinal);

    public long getRuntimeInMins(int ordinal);

    public Long getRuntimeInMinsBoxed(int ordinal);

    public CSMReviewTypeAPI getTypeAPI();

}