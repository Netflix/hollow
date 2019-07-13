package com.netflix.vms.transformer.data.gen.exhibitDealAttribute;

import com.netflix.hollow.api.testdata.HollowTestDataset;

public class ExhibitDealAttributeV1TestData extends HollowTestDataset {

    public BooleanTestData Boolean(BooleanTestData.BooleanField... fields) {
        BooleanTestData rec = BooleanTestData.Boolean(fields);
        add(rec);
        return rec;
    }

    public DisallowedAssetBundleEntryTestData DisallowedAssetBundleEntry(DisallowedAssetBundleEntryTestData.DisallowedAssetBundleEntryField... fields) {
        DisallowedAssetBundleEntryTestData rec = DisallowedAssetBundleEntryTestData.DisallowedAssetBundleEntry(fields);
        add(rec);
        return rec;
    }

    public LongTestData Long(LongTestData.LongField... fields) {
        LongTestData rec = LongTestData.Long(fields);
        add(rec);
        return rec;
    }

    public SetOfDisallowedAssetBundleEntryTestData SetOfDisallowedAssetBundleEntry(DisallowedAssetBundleEntryTestData... elements) {
        SetOfDisallowedAssetBundleEntryTestData rec = SetOfDisallowedAssetBundleEntryTestData.SetOfDisallowedAssetBundleEntry(elements);
        add(rec);
        return rec;
    }

    public SetOfStringTestData SetOfString(StringTestData... elements) {
        SetOfStringTestData rec = SetOfStringTestData.SetOfString(elements);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

    public VmsAttributeFeedEntryTestData VmsAttributeFeedEntry(VmsAttributeFeedEntryTestData.VmsAttributeFeedEntryField... fields) {
        VmsAttributeFeedEntryTestData rec = VmsAttributeFeedEntryTestData.VmsAttributeFeedEntry(fields);
        add(rec);
        return rec;
    }

}