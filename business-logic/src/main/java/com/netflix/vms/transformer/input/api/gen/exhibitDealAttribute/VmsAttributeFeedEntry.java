package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class VmsAttributeFeedEntry extends HollowObject {

    public VmsAttributeFeedEntry(VmsAttributeFeedEntryDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Long getMovieIdBoxed() {
        return delegate().getMovieIdBoxed(ordinal);
    }

    public long getMovieId() {
        return delegate().getMovieId(ordinal);
    }

    public HLong getMovieIdHollowReference() {
        int refOrdinal = delegate().getMovieIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public Long getDealIdBoxed() {
        return delegate().getDealIdBoxed(ordinal);
    }

    public long getDealId() {
        return delegate().getDealId(ordinal);
    }

    public HLong getDealIdHollowReference() {
        int refOrdinal = delegate().getDealIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public String getCountryCode() {
        return delegate().getCountryCode(ordinal);
    }

    public boolean isCountryCodeEqual(String testValue) {
        return delegate().isCountryCodeEqual(ordinal, testValue);
    }

    public HString getCountryCodeHollowReference() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public Boolean getDayAfterBroadcastBoxed() {
        return delegate().getDayAfterBroadcastBoxed(ordinal);
    }

    public boolean getDayAfterBroadcast() {
        return delegate().getDayAfterBroadcast(ordinal);
    }

    public HBoolean getDayAfterBroadcastHollowReference() {
        int refOrdinal = delegate().getDayAfterBroadcastOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHBoolean(refOrdinal);
    }

    public Boolean getDayOfBroadcastBoxed() {
        return delegate().getDayOfBroadcastBoxed(ordinal);
    }

    public boolean getDayOfBroadcast() {
        return delegate().getDayOfBroadcast(ordinal);
    }

    public HBoolean getDayOfBroadcastHollowReference() {
        int refOrdinal = delegate().getDayOfBroadcastOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHBoolean(refOrdinal);
    }

    public Long getPrePromotionDaysBoxed() {
        return delegate().getPrePromotionDaysBoxed(ordinal);
    }

    public long getPrePromotionDays() {
        return delegate().getPrePromotionDays(ordinal);
    }

    public HLong getPrePromotionDaysHollowReference() {
        int refOrdinal = delegate().getPrePromotionDaysOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHLong(refOrdinal);
    }

    public SetOfDisallowedAssetBundleEntry getDisallowedAssetBundles() {
        int refOrdinal = delegate().getDisallowedAssetBundlesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfDisallowedAssetBundleEntry(refOrdinal);
    }

    public ExhibitDealAttributeV1API api() {
        return typeApi().getAPI();
    }

    public VmsAttributeFeedEntryTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected VmsAttributeFeedEntryDelegate delegate() {
        return (VmsAttributeFeedEntryDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code VmsAttributeFeedEntry} that has a primary key.
     * The primary key is represented by the class {@link VmsAttributeFeedEntry.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<VmsAttributeFeedEntry, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, VmsAttributeFeedEntry.class)
            .bindToPrimaryKey()
            .usingBean(VmsAttributeFeedEntry.Key.class);
    }

    public static class Key {
        @FieldPath("movieId")
        public final long movieId;

        @FieldPath("dealId")
        public final long dealId;

        @FieldPath("countryCode")
        public final String countryCode;

        public Key(long movieId, long dealId, String countryCode) {
            this.movieId = movieId;
            this.dealId = dealId;
            this.countryCode = countryCode;
        }
    }

}