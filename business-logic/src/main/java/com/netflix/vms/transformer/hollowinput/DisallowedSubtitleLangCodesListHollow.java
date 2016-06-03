package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowList;
import com.netflix.hollow.HollowListSchema;
import com.netflix.hollow.objects.delegate.HollowListDelegate;
import com.netflix.hollow.objects.generic.GenericHollowRecordHelper;

public class DisallowedSubtitleLangCodesListHollow extends HollowList<DisallowedSubtitleLangCodeHollow> {

    public DisallowedSubtitleLangCodesListHollow(HollowListDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
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