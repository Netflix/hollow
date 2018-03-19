package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CertificationsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CertificationsHollow> {

    public CertificationsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public CertificationsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Certifications")).getPrimaryKey().getFieldPaths());
    }

    public CertificationsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CertificationsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Certifications", isListenToDataRefreah, fieldPaths);
    }

    public CertificationsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCertificationsHollow(ordinal);
    }

}