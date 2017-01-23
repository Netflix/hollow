package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class ArtworkDerivativeSetHollow extends HollowSet<ArtworkDerivativeHollow> {

    public ArtworkDerivativeSetHollow(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public ArtworkDerivativeHollow instantiateElement(int ordinal) {
        return (ArtworkDerivativeHollow) api().getArtworkDerivativeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkDerivativeSetTypeAPI typeApi() {
        return (ArtworkDerivativeSetTypeAPI) delegate.getTypeAPI();
    }

}