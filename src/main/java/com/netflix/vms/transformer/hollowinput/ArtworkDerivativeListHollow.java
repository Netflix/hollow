package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ArtworkDerivativeListHollow extends HollowList<ArtworkDerivativeHollow> {

    public ArtworkDerivativeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArtworkDerivativeHollow instantiateElement(int ordinal) {
        return (ArtworkDerivativeHollow) api().getArtworkDerivativeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkDerivativeListTypeAPI typeApi() {
        return (ArtworkDerivativeListTypeAPI) delegate.getTypeAPI();
    }

}