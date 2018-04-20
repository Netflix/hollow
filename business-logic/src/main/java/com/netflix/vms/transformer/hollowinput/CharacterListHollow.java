package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class CharacterListHollow extends HollowList<PersonCharacterHollow> {

    public CharacterListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public PersonCharacterHollow instantiateElement(int ordinal) {
        return (PersonCharacterHollow) api().getPersonCharacterHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CharacterListTypeAPI typeApi() {
        return (CharacterListTypeAPI) delegate.getTypeAPI();
    }

}