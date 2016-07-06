package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CharacterQuoteListHollow extends HollowList<CharacterQuoteHollow> {

    public CharacterQuoteListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CharacterQuoteHollow instantiateElement(int ordinal) {
        return (CharacterQuoteHollow) api().getCharacterQuoteHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterQuoteListTypeAPI typeApi() {
        return (CharacterQuoteListTypeAPI) delegate.getTypeAPI();
    }

}