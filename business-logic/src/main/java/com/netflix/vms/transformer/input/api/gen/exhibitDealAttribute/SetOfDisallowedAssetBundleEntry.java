package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowRecordHelper;

@SuppressWarnings("all")
public class SetOfDisallowedAssetBundleEntry extends HollowSet<DisallowedAssetBundleEntry> {

    public SetOfDisallowedAssetBundleEntry(HollowSetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    @Override
    public DisallowedAssetBundleEntry instantiateElement(int ordinal) {
        return (DisallowedAssetBundleEntry) api().getDisallowedAssetBundleEntry(ordinal);
    }

    @Override
    public boolean equalsElement(int elementOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getElementType(), elementOrdinal, testObject);
    }

    public ExhibitDealAttributeV1API api() {
        return typeApi().getAPI();
    }

    public SetOfDisallowedAssetBundleEntryTypeAPI typeApi() {
        return (SetOfDisallowedAssetBundleEntryTypeAPI) delegate.getTypeAPI();
    }

}