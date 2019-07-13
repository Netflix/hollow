package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VideoGeneral extends HollowObject {

    public VideoGeneral(VideoGeneralDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public boolean getTv() {
        return delegate().getTv(ordinal);
    }

    public Boolean getTvBoxed() {
        return delegate().getTvBoxed(ordinal);
    }

    public VideoGeneralAliasList getAliases() {
        int refOrdinal = delegate().getAliasesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralAliasList(refOrdinal);
    }

    public String getVideoType() {
        return delegate().getVideoType(ordinal);
    }

    public boolean isVideoTypeEqual(String testValue) {
        return delegate().isVideoTypeEqual(ordinal, testValue);
    }

    public HString getVideoTypeHollowReference() {
        int refOrdinal = delegate().getVideoTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getRuntime() {
        return delegate().getRuntime(ordinal);
    }

    public Integer getRuntimeBoxed() {
        return delegate().getRuntimeBoxed(ordinal);
    }

    public String getSupplementalSubType() {
        return delegate().getSupplementalSubType(ordinal);
    }

    public boolean isSupplementalSubTypeEqual(String testValue) {
        return delegate().isSupplementalSubTypeEqual(ordinal, testValue);
    }

    public HString getSupplementalSubTypeHollowReference() {
        int refOrdinal = delegate().getSupplementalSubTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getFirstReleaseYear() {
        return delegate().getFirstReleaseYear(ordinal);
    }

    public Integer getFirstReleaseYearBoxed() {
        return delegate().getFirstReleaseYearBoxed(ordinal);
    }

    public boolean getTestTitle() {
        return delegate().getTestTitle(ordinal);
    }

    public Boolean getTestTitleBoxed() {
        return delegate().getTestTitleBoxed(ordinal);
    }

    public String getOriginalLanguageBcpCode() {
        return delegate().getOriginalLanguageBcpCode(ordinal);
    }

    public boolean isOriginalLanguageBcpCodeEqual(String testValue) {
        return delegate().isOriginalLanguageBcpCodeEqual(ordinal, testValue);
    }

    public HString getOriginalLanguageBcpCodeHollowReference() {
        int refOrdinal = delegate().getOriginalLanguageBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getMetadataReleaseDays() {
        return delegate().getMetadataReleaseDays(ordinal);
    }

    public Integer getMetadataReleaseDaysBoxed() {
        return delegate().getMetadataReleaseDaysBoxed(ordinal);
    }

    public String getOriginCountryCode() {
        return delegate().getOriginCountryCode(ordinal);
    }

    public boolean isOriginCountryCodeEqual(String testValue) {
        return delegate().isOriginCountryCodeEqual(ordinal, testValue);
    }

    public HString getOriginCountryCodeHollowReference() {
        int refOrdinal = delegate().getOriginCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getOriginalTitle() {
        return delegate().getOriginalTitle(ordinal);
    }

    public boolean isOriginalTitleEqual(String testValue) {
        return delegate().isOriginalTitleEqual(ordinal, testValue);
    }

    public HString getOriginalTitleHollowReference() {
        int refOrdinal = delegate().getOriginalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public VideoGeneralTitleTypeList getTestTitleTypes() {
        int refOrdinal = delegate().getTestTitleTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralTitleTypeList(refOrdinal);
    }

    public String getOriginalTitleBcpCode() {
        return delegate().getOriginalTitleBcpCode(ordinal);
    }

    public boolean isOriginalTitleBcpCodeEqual(String testValue) {
        return delegate().isOriginalTitleBcpCodeEqual(ordinal, testValue);
    }

    public HString getOriginalTitleBcpCodeHollowReference() {
        int refOrdinal = delegate().getOriginalTitleBcpCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public String getInternalTitle() {
        return delegate().getInternalTitle(ordinal);
    }

    public boolean isInternalTitleEqual(String testValue) {
        return delegate().isInternalTitleEqual(ordinal, testValue);
    }

    public HString getInternalTitleHollowReference() {
        int refOrdinal = delegate().getInternalTitleOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public VideoGeneralEpisodeTypeList getEpisodeTypes() {
        int refOrdinal = delegate().getEpisodeTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralEpisodeTypeList(refOrdinal);
    }

    public SetOfString getRegulatoryAdvisories() {
        int refOrdinal = delegate().getRegulatoryAdvisoriesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public boolean getActive() {
        return delegate().getActive(ordinal);
    }

    public Boolean getActiveBoxed() {
        return delegate().getActiveBoxed(ordinal);
    }

    public int getDisplayRuntime() {
        return delegate().getDisplayRuntime(ordinal);
    }

    public Integer getDisplayRuntimeBoxed() {
        return delegate().getDisplayRuntimeBoxed(ordinal);
    }

    public VideoGeneralInteractiveData getInteractiveData() {
        int refOrdinal = delegate().getInteractiveDataOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getVideoGeneralInteractiveData(refOrdinal);
    }

    public VideoGeneralAPI api() {
        return typeApi().getAPI();
    }

    public VideoGeneralTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VideoGeneralDelegate delegate() {
        return (VideoGeneralDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code VideoGeneral} that has a primary key.
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
    public static UniqueKeyIndex<VideoGeneral, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, VideoGeneral.class)
            .bindToPrimaryKey()
            .usingPath("videoId", long.class);
    }

}