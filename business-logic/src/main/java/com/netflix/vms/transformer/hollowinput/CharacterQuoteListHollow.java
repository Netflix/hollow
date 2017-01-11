package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class CharacterQuoteListHollow extends HollowList<CharacterQuoteHollow> {

    public CharacterQuoteListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
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