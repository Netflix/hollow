package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AwardsHollow extends HollowObject {

    public AwardsHollow(AwardsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getAwardId() {
        return delegate().getAwardId(ordinal);
    }

    public Long _getAwardIdBoxed() {
        return delegate().getAwardIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getAwardName() {
        int refOrdinal = delegate().getAwardNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getAlternateName() {
        int refOrdinal = delegate().getAlternateNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public AwardsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected AwardsDelegate delegate() {
        return (AwardsDelegate)delegate;
    }

}