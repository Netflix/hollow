package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class IPLDerivativeGroup extends HollowObject {

    public IPLDerivativeGroup(IPLDerivativeGroupDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getExternalId() {
        return delegate().getExternalId(ordinal);
    }

    public boolean isExternalIdEqual(String testValue) {
        return delegate().isExternalIdEqual(ordinal, testValue);
    }

    public HString getExternalIdHollowReference() {
        int refOrdinal = delegate().getExternalIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getSubmission() {
        return delegate().getSubmission(ordinal);
    }

    public Integer getSubmissionBoxed() {
        return delegate().getSubmissionBoxed(ordinal);
    }

    public String getImageType() {
        return delegate().getImageType(ordinal);
    }

    public boolean isImageTypeEqual(String testValue) {
        return delegate().isImageTypeEqual(ordinal, testValue);
    }

    public HString getImageTypeHollowReference() {
        int refOrdinal = delegate().getImageTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public IPLDerivativeSet getDerivatives() {
        int refOrdinal = delegate().getDerivativesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getIPLDerivativeSet(refOrdinal);
    }

    public MceImageV3API api() {
        return typeApi().getAPI();
    }

    public IPLDerivativeGroupTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IPLDerivativeGroupDelegate delegate() {
        return (IPLDerivativeGroupDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code IPLDerivativeGroup} that has a primary key.
     * The primary key is represented by the class {@link IPLDerivativeGroup.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<IPLDerivativeGroup, Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, IPLDerivativeGroup.class)
            .bindToPrimaryKey()
            .usingBean(IPLDerivativeGroup.Key.class);
    }

    public static class Key {
        @FieldPath("externalId")
        public final String externalId;

        @FieldPath("imageType")
        public final String imageType;

        @FieldPath("submission")
        public final int submission;

        public Key(String externalId, String imageType, int submission) {
            this.externalId = externalId;
            this.imageType = imageType;
            this.submission = submission;
        }
    }

}