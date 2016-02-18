package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CharacterArtworkLocaleListHollow extends HollowList<CharacterArtworkLocaleHollow> {

    public CharacterArtworkLocaleListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CharacterArtworkLocaleHollow instantiateElement(int ordinal) {
        return (CharacterArtworkLocaleHollow) api().getCharacterArtworkLocaleHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArtworkLocaleListTypeAPI typeApi() {
        return (CharacterArtworkLocaleListTypeAPI) delegate.getTypeAPI();
    }

}