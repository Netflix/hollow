package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CSMReviewTypeAPI extends HollowObjectTypeAPI {

    private final CSMReviewDelegateLookupImpl delegateLookupImpl;

    CSMReviewTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "videoId",
            "studio",
            "sexualContentAlert",
            "consumerismAlert",
            "link",
            "otherChoices",
            "plasticReleaseDate",
            "ageExplanation",
            "sexualContent",
            "title",
            "ageRecommendation",
            "violenceAlert",
            "reviewerName",
            "socialBehavior",
            "parentsNeedToKnow",
            "dat",
            "isItAnyGood",
            "genre",
            "socialBehaviorAlert",
            "consumerism",
            "whatsTheStory",
            "castMemberNames",
            "violenceNote",
            "languageNote",
            "releaseDate",
            "languageAlert",
            "mediaType",
            "stars",
            "directorNames",
            "datAlert",
            "oneLiner",
            "greenBeginsAge",
            "redEndsAge",
            "messageAlert",
            "mpaaRating",
            "mpaaExplanation",
            "runtimeInMins"
        });
        this.delegateLookupImpl = new CSMReviewDelegateLookupImpl(this);
    }

    public long getVideoId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "videoId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getVideoIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "videoId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getStudioOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "studio");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getStudioTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getSexualContentAlert(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "sexualContentAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getSexualContentAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "sexualContentAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getConsumerismAlert(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "consumerismAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getConsumerismAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "consumerismAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getLinkOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "link");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public StringTypeAPI getLinkTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getOtherChoicesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "otherChoices");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getOtherChoicesTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getPlasticReleaseDateOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "plasticReleaseDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public DateTypeAPI getPlasticReleaseDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getAgeExplanationOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "ageExplanation");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public StringTypeAPI getAgeExplanationTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSexualContentOrdinal(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "sexualContent");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[8]);
    }

    public StringTypeAPI getSexualContentTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getTitleOrdinal(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "title");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[9]);
    }

    public StringTypeAPI getTitleTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getAgeRecommendation(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "ageRecommendation");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[10]);
    }

    public Long getAgeRecommendationBoxed(int ordinal) {
        long l;
        if(fieldIndex[10] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "ageRecommendation");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[10]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[10]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getViolenceAlert(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "violenceAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[11]);
    }

    public Long getViolenceAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[11] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "violenceAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[11]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[11]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getReviewerNameOrdinal(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "reviewerName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[12]);
    }

    public StringTypeAPI getReviewerNameTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getSocialBehaviorOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "socialBehavior");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public StringTypeAPI getSocialBehaviorTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getParentsNeedToKnowOrdinal(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "parentsNeedToKnow");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[14]);
    }

    public StringTypeAPI getParentsNeedToKnowTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDatOrdinal(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "dat");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[15]);
    }

    public StringTypeAPI getDatTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getIsItAnyGoodOrdinal(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "isItAnyGood");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[16]);
    }

    public StringTypeAPI getIsItAnyGoodTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getGenreOrdinal(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "genre");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[17]);
    }

    public StringTypeAPI getGenreTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getSocialBehaviorAlert(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "socialBehaviorAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[18]);
    }

    public Long getSocialBehaviorAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[18] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "socialBehaviorAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[18]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[18]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getConsumerismOrdinal(int ordinal) {
        if(fieldIndex[19] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "consumerism");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[19]);
    }

    public StringTypeAPI getConsumerismTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getWhatsTheStoryOrdinal(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "whatsTheStory");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[20]);
    }

    public StringTypeAPI getWhatsTheStoryTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getCastMemberNamesOrdinal(int ordinal) {
        if(fieldIndex[21] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "castMemberNames");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[21]);
    }

    public StringTypeAPI getCastMemberNamesTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getViolenceNoteOrdinal(int ordinal) {
        if(fieldIndex[22] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "violenceNote");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[22]);
    }

    public StringTypeAPI getViolenceNoteTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getLanguageNoteOrdinal(int ordinal) {
        if(fieldIndex[23] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "languageNote");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[23]);
    }

    public StringTypeAPI getLanguageNoteTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getReleaseDateOrdinal(int ordinal) {
        if(fieldIndex[24] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "releaseDate");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[24]);
    }

    public DateTypeAPI getReleaseDateTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public long getLanguageAlert(int ordinal) {
        if(fieldIndex[25] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "languageAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[25]);
    }

    public Long getLanguageAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[25] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "languageAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[25]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[25]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMediaTypeOrdinal(int ordinal) {
        if(fieldIndex[26] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "mediaType");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[26]);
    }

    public StringTypeAPI getMediaTypeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getStars(int ordinal) {
        if(fieldIndex[27] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "stars");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[27]);
    }

    public Long getStarsBoxed(int ordinal) {
        long l;
        if(fieldIndex[27] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "stars");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[27]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[27]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDirectorNamesOrdinal(int ordinal) {
        if(fieldIndex[28] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "directorNames");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[28]);
    }

    public StringTypeAPI getDirectorNamesTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getDatAlert(int ordinal) {
        if(fieldIndex[29] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "datAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[29]);
    }

    public Long getDatAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[29] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "datAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[29]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[29]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getOneLinerOrdinal(int ordinal) {
        if(fieldIndex[30] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "oneLiner");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[30]);
    }

    public StringTypeAPI getOneLinerTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getGreenBeginsAge(int ordinal) {
        if(fieldIndex[31] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "greenBeginsAge");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[31]);
    }

    public Long getGreenBeginsAgeBoxed(int ordinal) {
        long l;
        if(fieldIndex[31] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "greenBeginsAge");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[31]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[31]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getRedEndsAge(int ordinal) {
        if(fieldIndex[32] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "redEndsAge");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[32]);
    }

    public Long getRedEndsAgeBoxed(int ordinal) {
        long l;
        if(fieldIndex[32] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "redEndsAge");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[32]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[32]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMessageAlert(int ordinal) {
        if(fieldIndex[33] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "messageAlert");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[33]);
    }

    public Long getMessageAlertBoxed(int ordinal) {
        long l;
        if(fieldIndex[33] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "messageAlert");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[33]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[33]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMpaaRatingOrdinal(int ordinal) {
        if(fieldIndex[34] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "mpaaRating");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[34]);
    }

    public StringTypeAPI getMpaaRatingTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getMpaaExplanationOrdinal(int ordinal) {
        if(fieldIndex[35] == -1)
            return missingDataHandler().handleReferencedOrdinal("CSMReview", ordinal, "mpaaExplanation");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[35]);
    }

    public StringTypeAPI getMpaaExplanationTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getRuntimeInMins(int ordinal) {
        if(fieldIndex[36] == -1)
            return missingDataHandler().handleLong("CSMReview", ordinal, "runtimeInMins");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[36]);
    }

    public Long getRuntimeInMinsBoxed(int ordinal) {
        long l;
        if(fieldIndex[36] == -1) {
            l = missingDataHandler().handleLong("CSMReview", ordinal, "runtimeInMins");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[36]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[36]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CSMReviewDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}