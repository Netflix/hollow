package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class CSMReviewDelegateLookupImpl extends HollowObjectAbstractDelegate implements CSMReviewDelegate {

    private final CSMReviewTypeAPI typeAPI;

    public CSMReviewDelegateLookupImpl(CSMReviewTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getVideoId(int ordinal) {
        return typeAPI.getVideoId(ordinal);
    }

    public Long getVideoIdBoxed(int ordinal) {
        return typeAPI.getVideoIdBoxed(ordinal);
    }

    public int getStudioOrdinal(int ordinal) {
        return typeAPI.getStudioOrdinal(ordinal);
    }

    public long getSexualContentAlert(int ordinal) {
        return typeAPI.getSexualContentAlert(ordinal);
    }

    public Long getSexualContentAlertBoxed(int ordinal) {
        return typeAPI.getSexualContentAlertBoxed(ordinal);
    }

    public long getConsumerismAlert(int ordinal) {
        return typeAPI.getConsumerismAlert(ordinal);
    }

    public Long getConsumerismAlertBoxed(int ordinal) {
        return typeAPI.getConsumerismAlertBoxed(ordinal);
    }

    public int getLinkOrdinal(int ordinal) {
        return typeAPI.getLinkOrdinal(ordinal);
    }

    public int getOtherChoicesOrdinal(int ordinal) {
        return typeAPI.getOtherChoicesOrdinal(ordinal);
    }

    public int getPlasticReleaseDateOrdinal(int ordinal) {
        return typeAPI.getPlasticReleaseDateOrdinal(ordinal);
    }

    public int getAgeExplanationOrdinal(int ordinal) {
        return typeAPI.getAgeExplanationOrdinal(ordinal);
    }

    public int getSexualContentOrdinal(int ordinal) {
        return typeAPI.getSexualContentOrdinal(ordinal);
    }

    public int getTitleOrdinal(int ordinal) {
        return typeAPI.getTitleOrdinal(ordinal);
    }

    public long getAgeRecommendation(int ordinal) {
        return typeAPI.getAgeRecommendation(ordinal);
    }

    public Long getAgeRecommendationBoxed(int ordinal) {
        return typeAPI.getAgeRecommendationBoxed(ordinal);
    }

    public long getViolenceAlert(int ordinal) {
        return typeAPI.getViolenceAlert(ordinal);
    }

    public Long getViolenceAlertBoxed(int ordinal) {
        return typeAPI.getViolenceAlertBoxed(ordinal);
    }

    public int getReviewerNameOrdinal(int ordinal) {
        return typeAPI.getReviewerNameOrdinal(ordinal);
    }

    public int getSocialBehaviorOrdinal(int ordinal) {
        return typeAPI.getSocialBehaviorOrdinal(ordinal);
    }

    public int getParentsNeedToKnowOrdinal(int ordinal) {
        return typeAPI.getParentsNeedToKnowOrdinal(ordinal);
    }

    public int getDatOrdinal(int ordinal) {
        return typeAPI.getDatOrdinal(ordinal);
    }

    public int getIsItAnyGoodOrdinal(int ordinal) {
        return typeAPI.getIsItAnyGoodOrdinal(ordinal);
    }

    public int getGenreOrdinal(int ordinal) {
        return typeAPI.getGenreOrdinal(ordinal);
    }

    public long getSocialBehaviorAlert(int ordinal) {
        return typeAPI.getSocialBehaviorAlert(ordinal);
    }

    public Long getSocialBehaviorAlertBoxed(int ordinal) {
        return typeAPI.getSocialBehaviorAlertBoxed(ordinal);
    }

    public int getConsumerismOrdinal(int ordinal) {
        return typeAPI.getConsumerismOrdinal(ordinal);
    }

    public int getWhatsTheStoryOrdinal(int ordinal) {
        return typeAPI.getWhatsTheStoryOrdinal(ordinal);
    }

    public int getCastMemberNamesOrdinal(int ordinal) {
        return typeAPI.getCastMemberNamesOrdinal(ordinal);
    }

    public int getViolenceNoteOrdinal(int ordinal) {
        return typeAPI.getViolenceNoteOrdinal(ordinal);
    }

    public int getLanguageNoteOrdinal(int ordinal) {
        return typeAPI.getLanguageNoteOrdinal(ordinal);
    }

    public int getReleaseDateOrdinal(int ordinal) {
        return typeAPI.getReleaseDateOrdinal(ordinal);
    }

    public long getLanguageAlert(int ordinal) {
        return typeAPI.getLanguageAlert(ordinal);
    }

    public Long getLanguageAlertBoxed(int ordinal) {
        return typeAPI.getLanguageAlertBoxed(ordinal);
    }

    public int getMediaTypeOrdinal(int ordinal) {
        return typeAPI.getMediaTypeOrdinal(ordinal);
    }

    public long getStars(int ordinal) {
        return typeAPI.getStars(ordinal);
    }

    public Long getStarsBoxed(int ordinal) {
        return typeAPI.getStarsBoxed(ordinal);
    }

    public int getDirectorNamesOrdinal(int ordinal) {
        return typeAPI.getDirectorNamesOrdinal(ordinal);
    }

    public long getDatAlert(int ordinal) {
        return typeAPI.getDatAlert(ordinal);
    }

    public Long getDatAlertBoxed(int ordinal) {
        return typeAPI.getDatAlertBoxed(ordinal);
    }

    public int getOneLinerOrdinal(int ordinal) {
        return typeAPI.getOneLinerOrdinal(ordinal);
    }

    public long getGreenBeginsAge(int ordinal) {
        return typeAPI.getGreenBeginsAge(ordinal);
    }

    public Long getGreenBeginsAgeBoxed(int ordinal) {
        return typeAPI.getGreenBeginsAgeBoxed(ordinal);
    }

    public long getRedEndsAge(int ordinal) {
        return typeAPI.getRedEndsAge(ordinal);
    }

    public Long getRedEndsAgeBoxed(int ordinal) {
        return typeAPI.getRedEndsAgeBoxed(ordinal);
    }

    public long getMessageAlert(int ordinal) {
        return typeAPI.getMessageAlert(ordinal);
    }

    public Long getMessageAlertBoxed(int ordinal) {
        return typeAPI.getMessageAlertBoxed(ordinal);
    }

    public int getMpaaRatingOrdinal(int ordinal) {
        return typeAPI.getMpaaRatingOrdinal(ordinal);
    }

    public int getMpaaExplanationOrdinal(int ordinal) {
        return typeAPI.getMpaaExplanationOrdinal(ordinal);
    }

    public long getRuntimeInMins(int ordinal) {
        return typeAPI.getRuntimeInMins(ordinal);
    }

    public Long getRuntimeInMinsBoxed(int ordinal) {
        return typeAPI.getRuntimeInMinsBoxed(ordinal);
    }

    public CSMReviewTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}