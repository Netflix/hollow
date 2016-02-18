package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CharacterArtworkDerivativeListHollow extends HollowList<CharacterArtworkDerivativeHollow> {

    public CharacterArtworkDerivativeListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CharacterArtworkDerivativeHollow instantiateElement(int ordinal) {
        return (CharacterArtworkDerivativeHollow) api().getCharacterArtworkDerivativeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArtworkDerivativeListTypeAPI typeApi() {
        return (CharacterArtworkDerivativeListTypeAPI) delegate.getTypeAPI();
    }

}