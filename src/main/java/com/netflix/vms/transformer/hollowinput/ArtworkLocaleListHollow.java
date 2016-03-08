package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class ArtworkLocaleListHollow extends HollowList<ArtworkLocaleHollow> {

    public ArtworkLocaleListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ArtworkLocaleHollow instantiateElement(int ordinal) {
        return (ArtworkLocaleHollow) api().getArtworkLocaleHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ArtworkLocaleListTypeAPI typeApi() {
        return (ArtworkLocaleListTypeAPI) delegate.getTypeAPI();
    }

}