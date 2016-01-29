package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class CSMReviewDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, CSMReviewDelegate {

    private final int studioOrdinal;
    private final Long sexualContentAlert;
    private final Long consumerismAlert;
    private final int linkOrdinal;
    private final int otherChoicesOrdinal;
    private final Long plasticReleaseDate;
    private final Long videoId;
    private final int ageExplanationOrdinal;
    private final int sexualContentOrdinal;
    private final int titleOrdinal;
    private final Long ageRecommendation;
    private final Long violenceAlert;
    private final int reviewerNameOrdinal;
    private final int socialBehaviorOrdinal;
    private final int parentsNeedToKnowOrdinal;
    private final int datOrdinal;
    private final int isItAnyGoodOrdinal;
    private final int genreOrdinal;
    private final Long socialBehaviorAlert;
    private final int consumerismOrdinal;
    private final int whatsTheStoryOrdinal;
    private final int castMemberNamesOrdinal;
    private final int violenceNoteOrdinal;
    private final int languageNoteOrdinal;
    private final Long releaseDate;
    private final Long languageAlert;
    private final int mediaTypeOrdinal;
    private final Long stars;
    private final int directorNamesOrdinal;
    private final Long datAlert;
    private final int oneLinerOrdinal;
    private final Long greenBeginsAge;
    private final Long redEndsAge;
    private final Long messageAlert;
    private final int mpaaRatingOrdinal;
    private final int mpaaExplanationOrdinal;
    private final Long runtimeInMins;
   private CSMReviewTypeAPI typeAPI;

    public CSMReviewDelegateCachedImpl(CSMReviewTypeAPI typeAPI, int ordinal) {
        this.studioOrdinal = typeAPI.getStudioOrdinal(ordinal);
        this.sexualContentAlert = typeAPI.getSexualContentAlertBoxed(ordinal);
        this.consumerismAlert = typeAPI.getConsumerismAlertBoxed(ordinal);
        this.linkOrdinal = typeAPI.getLinkOrdinal(ordinal);
        this.otherChoicesOrdinal = typeAPI.getOtherChoicesOrdinal(ordinal);
        this.plasticReleaseDate = typeAPI.getPlasticReleaseDateBoxed(ordinal);
        this.videoId = typeAPI.getVideoIdBoxed(ordinal);
        this.ageExplanationOrdinal = typeAPI.getAgeExplanationOrdinal(ordinal);
        this.sexualContentOrdinal = typeAPI.getSexualContentOrdinal(ordinal);
        this.titleOrdinal = typeAPI.getTitleOrdinal(ordinal);
        this.ageRecommendation = typeAPI.getAgeRecommendationBoxed(ordinal);
        this.violenceAlert = typeAPI.getViolenceAlertBoxed(ordinal);
        this.reviewerNameOrdinal = typeAPI.getReviewerNameOrdinal(ordinal);
        this.socialBehaviorOrdinal = typeAPI.getSocialBehaviorOrdinal(ordinal);
        this.parentsNeedToKnowOrdinal = typeAPI.getParentsNeedToKnowOrdinal(ordinal);
        this.datOrdinal = typeAPI.getDatOrdinal(ordinal);
        this.isItAnyGoodOrdinal = typeAPI.getIsItAnyGoodOrdinal(ordinal);
        this.genreOrdinal = typeAPI.getGenreOrdinal(ordinal);
        this.socialBehaviorAlert = typeAPI.getSocialBehaviorAlertBoxed(ordinal);
        this.consumerismOrdinal = typeAPI.getConsumerismOrdinal(ordinal);
        this.whatsTheStoryOrdinal = typeAPI.getWhatsTheStoryOrdinal(ordinal);
        this.castMemberNamesOrdinal = typeAPI.getCastMemberNamesOrdinal(ordinal);
        this.violenceNoteOrdinal = typeAPI.getViolenceNoteOrdinal(ordinal);
        this.languageNoteOrdinal = typeAPI.getLanguageNoteOrdinal(ordinal);
        this.releaseDate = typeAPI.getReleaseDateBoxed(ordinal);
        this.languageAlert = typeAPI.getLanguageAlertBoxed(ordinal);
        this.mediaTypeOrdinal = typeAPI.getMediaTypeOrdinal(ordinal);
        this.stars = typeAPI.getStarsBoxed(ordinal);
        this.directorNamesOrdinal = typeAPI.getDirectorNamesOrdinal(ordinal);
        this.datAlert = typeAPI.getDatAlertBoxed(ordinal);
        this.oneLinerOrdinal = typeAPI.getOneLinerOrdinal(ordinal);
        this.greenBeginsAge = typeAPI.getGreenBeginsAgeBoxed(ordinal);
        this.redEndsAge = typeAPI.getRedEndsAgeBoxed(ordinal);
        this.messageAlert = typeAPI.getMessageAlertBoxed(ordinal);
        this.mpaaRatingOrdinal = typeAPI.getMpaaRatingOrdinal(ordinal);
        this.mpaaExplanationOrdinal = typeAPI.getMpaaExplanationOrdinal(ordinal);
        this.runtimeInMins = typeAPI.getRuntimeInMinsBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getStudioOrdinal(int ordinal) {
        return studioOrdinal;
    }

    public long getSexualContentAlert(int ordinal) {
        return sexualContentAlert.longValue();
    }

    public Long getSexualContentAlertBoxed(int ordinal) {
        return sexualContentAlert;
    }

    public long getConsumerismAlert(int ordinal) {
        return consumerismAlert.longValue();
    }

    public Long getConsumerismAlertBoxed(int ordinal) {
        return consumerismAlert;
    }

    public int getLinkOrdinal(int ordinal) {
        return linkOrdinal;
    }

    public int getOtherChoicesOrdinal(int ordinal) {
        return otherChoicesOrdinal;
    }

    public long getPlasticReleaseDate(int ordinal) {
        return plasticReleaseDate.longValue();
    }

    public Long getPlasticReleaseDateBoxed(int ordinal) {
        return plasticReleaseDate;
    }

    public long getVideoId(int ordinal) {
        return videoId.longValue();
    }

    public Long getVideoIdBoxed(int ordinal) {
        return videoId;
    }

    public int getAgeExplanationOrdinal(int ordinal) {
        return ageExplanationOrdinal;
    }

    public int getSexualContentOrdinal(int ordinal) {
        return sexualContentOrdinal;
    }

    public int getTitleOrdinal(int ordinal) {
        return titleOrdinal;
    }

    public long getAgeRecommendation(int ordinal) {
        return ageRecommendation.longValue();
    }

    public Long getAgeRecommendationBoxed(int ordinal) {
        return ageRecommendation;
    }

    public long getViolenceAlert(int ordinal) {
        return violenceAlert.longValue();
    }

    public Long getViolenceAlertBoxed(int ordinal) {
        return violenceAlert;
    }

    public int getReviewerNameOrdinal(int ordinal) {
        return reviewerNameOrdinal;
    }

    public int getSocialBehaviorOrdinal(int ordinal) {
        return socialBehaviorOrdinal;
    }

    public int getParentsNeedToKnowOrdinal(int ordinal) {
        return parentsNeedToKnowOrdinal;
    }

    public int getDatOrdinal(int ordinal) {
        return datOrdinal;
    }

    public int getIsItAnyGoodOrdinal(int ordinal) {
        return isItAnyGoodOrdinal;
    }

    public int getGenreOrdinal(int ordinal) {
        return genreOrdinal;
    }

    public long getSocialBehaviorAlert(int ordinal) {
        return socialBehaviorAlert.longValue();
    }

    public Long getSocialBehaviorAlertBoxed(int ordinal) {
        return socialBehaviorAlert;
    }

    public int getConsumerismOrdinal(int ordinal) {
        return consumerismOrdinal;
    }

    public int getWhatsTheStoryOrdinal(int ordinal) {
        return whatsTheStoryOrdinal;
    }

    public int getCastMemberNamesOrdinal(int ordinal) {
        return castMemberNamesOrdinal;
    }

    public int getViolenceNoteOrdinal(int ordinal) {
        return violenceNoteOrdinal;
    }

    public int getLanguageNoteOrdinal(int ordinal) {
        return languageNoteOrdinal;
    }

    public long getReleaseDate(int ordinal) {
        return releaseDate.longValue();
    }

    public Long getReleaseDateBoxed(int ordinal) {
        return releaseDate;
    }

    public long getLanguageAlert(int ordinal) {
        return languageAlert.longValue();
    }

    public Long getLanguageAlertBoxed(int ordinal) {
        return languageAlert;
    }

    public int getMediaTypeOrdinal(int ordinal) {
        return mediaTypeOrdinal;
    }

    public long getStars(int ordinal) {
        return stars.longValue();
    }

    public Long getStarsBoxed(int ordinal) {
        return stars;
    }

    public int getDirectorNamesOrdinal(int ordinal) {
        return directorNamesOrdinal;
    }

    public long getDatAlert(int ordinal) {
        return datAlert.longValue();
    }

    public Long getDatAlertBoxed(int ordinal) {
        return datAlert;
    }

    public int getOneLinerOrdinal(int ordinal) {
        return oneLinerOrdinal;
    }

    public long getGreenBeginsAge(int ordinal) {
        return greenBeginsAge.longValue();
    }

    public Long getGreenBeginsAgeBoxed(int ordinal) {
        return greenBeginsAge;
    }

    public long getRedEndsAge(int ordinal) {
        return redEndsAge.longValue();
    }

    public Long getRedEndsAgeBoxed(int ordinal) {
        return redEndsAge;
    }

    public long getMessageAlert(int ordinal) {
        return messageAlert.longValue();
    }

    public Long getMessageAlertBoxed(int ordinal) {
        return messageAlert;
    }

    public int getMpaaRatingOrdinal(int ordinal) {
        return mpaaRatingOrdinal;
    }

    public int getMpaaExplanationOrdinal(int ordinal) {
        return mpaaExplanationOrdinal;
    }

    public long getRuntimeInMins(int ordinal) {
        return runtimeInMins.longValue();
    }

    public Long getRuntimeInMinsBoxed(int ordinal) {
        return runtimeInMins;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public CSMReviewTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (CSMReviewTypeAPI) typeAPI;
    }

}