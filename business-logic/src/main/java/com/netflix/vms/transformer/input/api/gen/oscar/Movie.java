package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class Movie extends HollowObject {

    public Movie(MovieDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public MovieId getMovieIdHollowReference() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieId(refOrdinal);
    }

    public MovieType getType() {
        int refOrdinal = delegate().getTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieType(refOrdinal);
    }

    public String getOriginalLanguageBcpCode() {
        return delegate().getOriginalLanguageBcpCode(ordinal);
    }

    public boolean isOriginalLanguageBcpCodeEqual(String testValue) {
        return delegate().isOriginalLanguageBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getOriginalLanguageBcpCodeHollowReference() {
        int refOrdinal = delegate().getOriginalLanguageBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getOriginalTitle() {
        return delegate().getOriginalTitle(ordinal);
    }

    public boolean isOriginalTitleEqual(String testValue) {
        return delegate().isOriginalTitleEqual(ordinal, testValue);
    }

    public MovieTitleString getOriginalTitleHollowReference() {
        int refOrdinal = delegate().getOriginalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getOriginalTitleBcpCode() {
        return delegate().getOriginalTitleBcpCode(ordinal);
    }

    public boolean isOriginalTitleBcpCodeEqual(String testValue) {
        return delegate().isOriginalTitleBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getOriginalTitleBcpCodeHollowReference() {
        int refOrdinal = delegate().getOriginalTitleBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getOriginalTitleConcat() {
        return delegate().getOriginalTitleConcat(ordinal);
    }

    public boolean isOriginalTitleConcatEqual(String testValue) {
        return delegate().isOriginalTitleConcatEqual(ordinal, testValue);
    }

    public MovieTitleString getOriginalTitleConcatHollowReference() {
        int refOrdinal = delegate().getOriginalTitleConcatOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getOriginalTitleConcatBcpCode() {
        return delegate().getOriginalTitleConcatBcpCode(ordinal);
    }

    public boolean isOriginalTitleConcatBcpCodeEqual(String testValue) {
        return delegate().isOriginalTitleConcatBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getOriginalTitleConcatBcpCodeHollowReference() {
        int refOrdinal = delegate().getOriginalTitleConcatBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getCountryOfOrigin() {
        return delegate().getCountryOfOrigin(ordinal);
    }

    public boolean isCountryOfOriginEqual(String testValue) {
        return delegate().isCountryOfOriginEqual(ordinal, testValue);
    }

    public CountryString getCountryOfOriginHollowReference() {
        int refOrdinal = delegate().getCountryOfOriginOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCountryString(refOrdinal);
    }

    public int getRunLenth() {
        return delegate().getRunLenth(ordinal);
    }

    public Integer getRunLenthBoxed() {
        return delegate().getRunLenthBoxed(ordinal);
    }

    public boolean getAvailableInPlastic() {
        return delegate().getAvailableInPlastic(ordinal);
    }

    public Boolean getAvailableInPlasticBoxed() {
        return delegate().getAvailableInPlasticBoxed(ordinal);
    }

    public int getFirstReleaseYear() {
        return delegate().getFirstReleaseYear(ordinal);
    }

    public Integer getFirstReleaseYearBoxed() {
        return delegate().getFirstReleaseYearBoxed(ordinal);
    }

    public boolean getActive() {
        return delegate().getActive(ordinal);
    }

    public Boolean getActiveBoxed() {
        return delegate().getActiveBoxed(ordinal);
    }

    public boolean getTv() {
        return delegate().getTv(ordinal);
    }

    public Boolean getTvBoxed() {
        return delegate().getTvBoxed(ordinal);
    }

    public String getComment() {
        return delegate().getComment(ordinal);
    }

    public boolean isCommentEqual(String testValue) {
        return delegate().isCommentEqual(ordinal, testValue);
    }

    public HString getCommentHollowReference() {
        int refOrdinal = delegate().getCommentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public boolean getOriginal() {
        return delegate().getOriginal(ordinal);
    }

    public Boolean getOriginalBoxed() {
        return delegate().getOriginalBoxed(ordinal);
    }

    public String getSubtype() {
        return delegate().getSubtype(ordinal);
    }

    public boolean isSubtypeEqual(String testValue) {
        return delegate().isSubtypeEqual(ordinal, testValue);
    }

    public SupplementalSubtype getSubtypeHollowReference() {
        int refOrdinal = delegate().getSubtypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSupplementalSubtype(refOrdinal);
    }

    public boolean getTestTitle() {
        return delegate().getTestTitle(ordinal);
    }

    public Boolean getTestTitleBoxed() {
        return delegate().getTestTitleBoxed(ordinal);
    }

    public int getMetadataReleaseDays() {
        return delegate().getMetadataReleaseDays(ordinal);
    }

    public Integer getMetadataReleaseDaysBoxed() {
        return delegate().getMetadataReleaseDaysBoxed(ordinal);
    }

    public boolean getManualMetadataReleaseDaysUpdate() {
        return delegate().getManualMetadataReleaseDaysUpdate(ordinal);
    }

    public Boolean getManualMetadataReleaseDaysUpdateBoxed() {
        return delegate().getManualMetadataReleaseDaysUpdateBoxed(ordinal);
    }

    public String getInternalTitle() {
        return delegate().getInternalTitle(ordinal);
    }

    public boolean isInternalTitleEqual(String testValue) {
        return delegate().isInternalTitleEqual(ordinal, testValue);
    }

    public MovieTitleString getInternalTitleHollowReference() {
        int refOrdinal = delegate().getInternalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getInternalTitleBcpCode() {
        return delegate().getInternalTitleBcpCode(ordinal);
    }

    public boolean isInternalTitleBcpCodeEqual(String testValue) {
        return delegate().isInternalTitleBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getInternalTitleBcpCodeHollowReference() {
        int refOrdinal = delegate().getInternalTitleBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getInternalTitlePart() {
        return delegate().getInternalTitlePart(ordinal);
    }

    public boolean isInternalTitlePartEqual(String testValue) {
        return delegate().isInternalTitlePartEqual(ordinal, testValue);
    }

    public MovieTitleString getInternalTitlePartHollowReference() {
        int refOrdinal = delegate().getInternalTitlePartOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getInternalTitlePartBcpCode() {
        return delegate().getInternalTitlePartBcpCode(ordinal);
    }

    public boolean isInternalTitlePartBcpCodeEqual(String testValue) {
        return delegate().isInternalTitlePartBcpCodeEqual(ordinal, testValue);
    }

    public BcpCode getInternalTitlePartBcpCodeHollowReference() {
        int refOrdinal = delegate().getInternalTitlePartBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getBcpCode(refOrdinal);
    }

    public String getSearchTitle() {
        return delegate().getSearchTitle(ordinal);
    }

    public boolean isSearchTitleEqual(String testValue) {
        return delegate().isSearchTitleEqual(ordinal, testValue);
    }

    public MovieTitleString getSearchTitleHollowReference() {
        int refOrdinal = delegate().getSearchTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMovieTitleString(refOrdinal);
    }

    public String getDirector() {
        return delegate().getDirector(ordinal);
    }

    public boolean isDirectorEqual(String testValue) {
        return delegate().isDirectorEqual(ordinal, testValue);
    }

    public PersonName getDirectorHollowReference() {
        int refOrdinal = delegate().getDirectorOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonName(refOrdinal);
    }

    public String getCreator() {
        return delegate().getCreator(ordinal);
    }

    public boolean isCreatorEqual(String testValue) {
        return delegate().isCreatorEqual(ordinal, testValue);
    }

    public PersonName getCreatorHollowReference() {
        int refOrdinal = delegate().getCreatorOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonName(refOrdinal);
    }

    public String getForceReason() {
        return delegate().getForceReason(ordinal);
    }

    public boolean isForceReasonEqual(String testValue) {
        return delegate().isForceReasonEqual(ordinal, testValue);
    }

    public ForceReason getForceReasonHollowReference() {
        int refOrdinal = delegate().getForceReasonOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getForceReason(refOrdinal);
    }

    public boolean getVisible() {
        return delegate().getVisible(ordinal);
    }

    public Boolean getVisibleBoxed() {
        return delegate().getVisibleBoxed(ordinal);
    }

    public String getCreatedByTeam() {
        return delegate().getCreatedByTeam(ordinal);
    }

    public boolean isCreatedByTeamEqual(String testValue) {
        return delegate().isCreatedByTeamEqual(ordinal, testValue);
    }

    public HString getCreatedByTeamHollowReference() {
        int refOrdinal = delegate().getCreatedByTeamOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getUpdatedByTeam() {
        return delegate().getUpdatedByTeam(ordinal);
    }

    public boolean isUpdatedByTeamEqual(String testValue) {
        return delegate().isUpdatedByTeamEqual(ordinal, testValue);
    }

    public HString getUpdatedByTeamHollowReference() {
        int refOrdinal = delegate().getUpdatedByTeamOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public Long getDateCreatedBoxed() {
        return delegate().getDateCreatedBoxed(ordinal);
    }

    public long getDateCreated() {
        return delegate().getDateCreated(ordinal);
    }

    public Date getDateCreatedHollowReference() {
        int refOrdinal = delegate().getDateCreatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public Long getLastUpdatedBoxed() {
        return delegate().getLastUpdatedBoxed(ordinal);
    }

    public long getLastUpdated() {
        return delegate().getLastUpdated(ordinal);
    }

    public Date getLastUpdatedHollowReference() {
        int refOrdinal = delegate().getLastUpdatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDate(refOrdinal);
    }

    public String getCreatedBy() {
        return delegate().getCreatedBy(ordinal);
    }

    public boolean isCreatedByEqual(String testValue) {
        return delegate().isCreatedByEqual(ordinal, testValue);
    }

    public HString getCreatedByHollowReference() {
        int refOrdinal = delegate().getCreatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getUpdatedBy() {
        return delegate().getUpdatedBy(ordinal);
    }

    public boolean isUpdatedByEqual(String testValue) {
        return delegate().isUpdatedByEqual(ordinal, testValue);
    }

    public HString getUpdatedByHollowReference() {
        int refOrdinal = delegate().getUpdatedByOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public MovieTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieDelegate delegate() {
        return (MovieDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code Movie} that has a primary key.
     * The primary key is represented by the type {@code long}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<Movie, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, Movie.class)
            .bindToPrimaryKey()
            .usingPath("movieId", long.class);
    }

}