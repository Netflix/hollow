package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class MovieTypeAPI extends HollowObjectTypeAPI {

    private final MovieDelegateLookupImpl delegateLookupImpl;

    public MovieTypeAPI(OscarAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "movieId",
            "type",
            "originalLanguageBcpCode",
            "originalTitle",
            "originalTitleBcpCode",
            "originalTitleConcat",
            "originalTitleConcatBcpCode",
            "countryOfOrigin",
            "runLenth",
            "availableInPlastic",
            "firstReleaseYear",
            "active",
            "tv",
            "comment",
            "original",
            "subtype",
            "testTitle",
            "metadataReleaseDays",
            "manualMetadataReleaseDaysUpdate",
            "internalTitle",
            "internalTitleBcpCode",
            "internalTitlePart",
            "internalTitlePartBcpCode",
            "searchTitle",
            "director",
            "creator",
            "forceReason",
            "visible",
            "createdByTeam",
            "updatedByTeam",
            "dateCreated",
            "lastUpdated",
            "createdBy",
            "updatedBy"
        });
        this.delegateLookupImpl = new MovieDelegateLookupImpl(this);
    }

    public int getMovieIdOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "movieId");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MovieIdTypeAPI getMovieIdTypeAPI() {
        return getAPI().getMovieIdTypeAPI();
    }

    public int getTypeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "type");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public MovieTypeTypeAPI getTypeTypeAPI() {
        return getAPI().getMovieTypeTypeAPI();
    }

    public int getOriginalLanguageBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "originalLanguageBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public BcpCodeTypeAPI getOriginalLanguageBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getOriginalTitleOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "originalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public MovieTitleStringTypeAPI getOriginalTitleTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getOriginalTitleBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "originalTitleBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public BcpCodeTypeAPI getOriginalTitleBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getOriginalTitleConcatOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "originalTitleConcat");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public MovieTitleStringTypeAPI getOriginalTitleConcatTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getOriginalTitleConcatBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "originalTitleConcatBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public BcpCodeTypeAPI getOriginalTitleConcatBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getCountryOfOriginOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "countryOfOrigin");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public CountryStringTypeAPI getCountryOfOriginTypeAPI() {
        return getAPI().getCountryStringTypeAPI();
    }

    public int getRunLenth(int ordinal) {
        if(fieldIndex[8] == -1)
            return missingDataHandler().handleInt("Movie", ordinal, "runLenth");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[8]);
    }

    public Integer getRunLenthBoxed(int ordinal) {
        int i;
        if(fieldIndex[8] == -1) {
            i = missingDataHandler().handleInt("Movie", ordinal, "runLenth");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[8]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[8]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public boolean getAvailableInPlastic(int ordinal) {
        if(fieldIndex[9] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "availableInPlastic"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]));
    }

    public Boolean getAvailableInPlasticBoxed(int ordinal) {
        if(fieldIndex[9] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "availableInPlastic");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[9]);
    }



    public int getFirstReleaseYear(int ordinal) {
        if(fieldIndex[10] == -1)
            return missingDataHandler().handleInt("Movie", ordinal, "firstReleaseYear");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[10]);
    }

    public Integer getFirstReleaseYearBoxed(int ordinal) {
        int i;
        if(fieldIndex[10] == -1) {
            i = missingDataHandler().handleInt("Movie", ordinal, "firstReleaseYear");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[10]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[10]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public boolean getActive(int ordinal) {
        if(fieldIndex[11] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "active"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]));
    }

    public Boolean getActiveBoxed(int ordinal) {
        if(fieldIndex[11] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "active");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[11]);
    }



    public boolean getTv(int ordinal) {
        if(fieldIndex[12] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "tv"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[12]));
    }

    public Boolean getTvBoxed(int ordinal) {
        if(fieldIndex[12] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "tv");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[12]);
    }



    public int getCommentOrdinal(int ordinal) {
        if(fieldIndex[13] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "comment");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[13]);
    }

    public StringTypeAPI getCommentTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public boolean getOriginal(int ordinal) {
        if(fieldIndex[14] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "original"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[14]));
    }

    public Boolean getOriginalBoxed(int ordinal) {
        if(fieldIndex[14] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "original");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[14]);
    }



    public int getSubtypeOrdinal(int ordinal) {
        if(fieldIndex[15] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "subtype");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[15]);
    }

    public SupplementalSubtypeTypeAPI getSubtypeTypeAPI() {
        return getAPI().getSupplementalSubtypeTypeAPI();
    }

    public boolean getTestTitle(int ordinal) {
        if(fieldIndex[16] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "testTitle"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[16]));
    }

    public Boolean getTestTitleBoxed(int ordinal) {
        if(fieldIndex[16] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "testTitle");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[16]);
    }



    public int getMetadataReleaseDays(int ordinal) {
        if(fieldIndex[17] == -1)
            return missingDataHandler().handleInt("Movie", ordinal, "metadataReleaseDays");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[17]);
    }

    public Integer getMetadataReleaseDaysBoxed(int ordinal) {
        int i;
        if(fieldIndex[17] == -1) {
            i = missingDataHandler().handleInt("Movie", ordinal, "metadataReleaseDays");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[17]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[17]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }



    public boolean getManualMetadataReleaseDaysUpdate(int ordinal) {
        if(fieldIndex[18] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "manualMetadataReleaseDaysUpdate"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[18]));
    }

    public Boolean getManualMetadataReleaseDaysUpdateBoxed(int ordinal) {
        if(fieldIndex[18] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "manualMetadataReleaseDaysUpdate");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[18]);
    }



    public int getInternalTitleOrdinal(int ordinal) {
        if(fieldIndex[19] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "internalTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[19]);
    }

    public MovieTitleStringTypeAPI getInternalTitleTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getInternalTitleBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[20] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "internalTitleBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[20]);
    }

    public BcpCodeTypeAPI getInternalTitleBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getInternalTitlePartOrdinal(int ordinal) {
        if(fieldIndex[21] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "internalTitlePart");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[21]);
    }

    public MovieTitleStringTypeAPI getInternalTitlePartTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getInternalTitlePartBcpCodeOrdinal(int ordinal) {
        if(fieldIndex[22] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "internalTitlePartBcpCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[22]);
    }

    public BcpCodeTypeAPI getInternalTitlePartBcpCodeTypeAPI() {
        return getAPI().getBcpCodeTypeAPI();
    }

    public int getSearchTitleOrdinal(int ordinal) {
        if(fieldIndex[23] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "searchTitle");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[23]);
    }

    public MovieTitleStringTypeAPI getSearchTitleTypeAPI() {
        return getAPI().getMovieTitleStringTypeAPI();
    }

    public int getDirectorOrdinal(int ordinal) {
        if(fieldIndex[24] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "director");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[24]);
    }

    public PersonNameTypeAPI getDirectorTypeAPI() {
        return getAPI().getPersonNameTypeAPI();
    }

    public int getCreatorOrdinal(int ordinal) {
        if(fieldIndex[25] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "creator");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[25]);
    }

    public PersonNameTypeAPI getCreatorTypeAPI() {
        return getAPI().getPersonNameTypeAPI();
    }

    public int getForceReasonOrdinal(int ordinal) {
        if(fieldIndex[26] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "forceReason");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[26]);
    }

    public ForceReasonTypeAPI getForceReasonTypeAPI() {
        return getAPI().getForceReasonTypeAPI();
    }

    public boolean getVisible(int ordinal) {
        if(fieldIndex[27] == -1)
            return Boolean.TRUE.equals(missingDataHandler().handleBoolean("Movie", ordinal, "visible"));
        return Boolean.TRUE.equals(getTypeDataAccess().readBoolean(ordinal, fieldIndex[27]));
    }

    public Boolean getVisibleBoxed(int ordinal) {
        if(fieldIndex[27] == -1)
            return missingDataHandler().handleBoolean("Movie", ordinal, "visible");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[27]);
    }



    public int getCreatedByTeamOrdinal(int ordinal) {
        if(fieldIndex[28] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "createdByTeam");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[28]);
    }

    public StringTypeAPI getCreatedByTeamTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByTeamOrdinal(int ordinal) {
        if(fieldIndex[29] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "updatedByTeam");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[29]);
    }

    public StringTypeAPI getUpdatedByTeamTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getDateCreatedOrdinal(int ordinal) {
        if(fieldIndex[30] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "dateCreated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[30]);
    }

    public DateTypeAPI getDateCreatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getLastUpdatedOrdinal(int ordinal) {
        if(fieldIndex[31] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "lastUpdated");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[31]);
    }

    public DateTypeAPI getLastUpdatedTypeAPI() {
        return getAPI().getDateTypeAPI();
    }

    public int getCreatedByOrdinal(int ordinal) {
        if(fieldIndex[32] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "createdBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[32]);
    }

    public StringTypeAPI getCreatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public int getUpdatedByOrdinal(int ordinal) {
        if(fieldIndex[33] == -1)
            return missingDataHandler().handleReferencedOrdinal("Movie", ordinal, "updatedBy");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[33]);
    }

    public StringTypeAPI getUpdatedByTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public MovieDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public OscarAPI getAPI() {
        return (OscarAPI) api;
    }

}