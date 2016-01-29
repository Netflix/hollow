package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class CharacterArrayOfQuotesHollow extends HollowList<CharacterQuotesHollow> {

    public CharacterArrayOfQuotesHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CharacterQuotesHollow instantiateElement(int ordinal) {
        return (CharacterQuotesHollow) api().getCharacterQuotesHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterArrayOfQuotesTypeAPI typeApi() {
        return (CharacterArrayOfQuotesTypeAPI) delegate.getTypeAPI();
    }

}