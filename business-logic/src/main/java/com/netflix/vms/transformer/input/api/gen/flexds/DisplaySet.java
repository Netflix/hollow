package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisplaySet extends HollowObject {

    public DisplaySet(DisplaySetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getSetId() {
        return delegate().getSetId(ordinal);
    }

    public Long getSetIdBoxed() {
        return delegate().getSetIdBoxed(ordinal);
    }

    public ListOfString getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getListOfString(refOrdinal);
    }

    public boolean getIsDefault() {
        return delegate().getIsDefault(ordinal);
    }

    public Boolean getIsDefaultBoxed() {
        return delegate().getIsDefaultBoxed(ordinal);
    }

    public SetOfString getDisplaySetTypes() {
        int refOrdinal = delegate().getDisplaySetTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfString(refOrdinal);
    }

    public SetOfContainer getContainers() {
        int refOrdinal = delegate().getContainersOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfContainer(refOrdinal);
    }

    public AuditGroup getCreated() {
        int refOrdinal = delegate().getCreatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAuditGroup(refOrdinal);
    }

    public AuditGroup getUpdated() {
        int refOrdinal = delegate().getUpdatedOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getAuditGroup(refOrdinal);
    }

    public FlexDSAPI api() {
        return typeApi().getAPI();
    }

    public DisplaySetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DisplaySetDelegate delegate() {
        return (DisplaySetDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code DisplaySet} that has a primary key.
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
    public static UniqueKeyIndex<DisplaySet, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, DisplaySet.class)
            .bindToPrimaryKey()
            .usingPath("setId", long.class);
    }

}