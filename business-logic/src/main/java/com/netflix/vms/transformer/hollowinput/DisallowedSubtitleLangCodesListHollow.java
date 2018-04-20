package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class DisallowedSubtitleLangCodesListHollow extends HollowList<DisallowedSubtitleLangCodeHollow> {

    public DisallowedSubtitleLangCodesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public DisallowedSubtitleLangCodeHollow instantiateElement(int ordinal) {
        return (DisallowedSubtitleLangCodeHollow) api().getDisallowedSubtitleLangCodeHollow(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DisallowedSubtitleLangCodesListTypeAPI typeApi() {
        return (DisallowedSubtitleLangCodesListTypeAPI) delegate.getTypeAPI();
    }

}