package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowSet;
import com.netflix.hollow.HollowSetSchema;
import com.netflix.hollow.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

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