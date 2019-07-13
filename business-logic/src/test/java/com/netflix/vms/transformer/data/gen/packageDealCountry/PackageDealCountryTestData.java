package com.netflix.vms.transformer.data.gen.packageDealCountry;

import com.netflix.hollow.api.testdata.HollowTestDataMapEntry;
import com.netflix.hollow.api.testdata.HollowTestDataset;

public class PackageDealCountryTestData extends HollowTestDataset {

    public BooleanTestData Boolean(BooleanTestData.BooleanField... fields) {
        BooleanTestData rec = BooleanTestData.Boolean(fields);
        add(rec);
        return rec;
    }

    public DealCountryGroupTestData DealCountryGroup(DealCountryGroupTestData.DealCountryGroupField... fields) {
        DealCountryGroupTestData rec = DealCountryGroupTestData.DealCountryGroup(fields);
        add(rec);
        return rec;
    }

    public ListOfDealCountryGroupTestData ListOfDealCountryGroup(DealCountryGroupTestData... elements) {
        ListOfDealCountryGroupTestData rec = ListOfDealCountryGroupTestData.ListOfDealCountryGroup(elements);
        add(rec);
        return rec;
    }

    public ListOfPackageTagsTestData ListOfPackageTags(StringTestData... elements) {
        ListOfPackageTagsTestData rec = ListOfPackageTagsTestData.ListOfPackageTags(elements);
        add(rec);
        return rec;
    }

    public LongTestData Long(LongTestData.LongField... fields) {
        LongTestData rec = LongTestData.Long(fields);
        add(rec);
        return rec;
    }

    @SafeVarargs
    public final MapOfStringToBooleanTestData MapOfStringToBoolean(HollowTestDataMapEntry<StringTestData, BooleanTestData>... entries) {
        MapOfStringToBooleanTestData rec = MapOfStringToBooleanTestData.MapOfStringToBoolean(entries);
        add(rec);
        return rec;
    }

    public PackageMovieDealCountryGroupTestData PackageMovieDealCountryGroup(PackageMovieDealCountryGroupTestData.PackageMovieDealCountryGroupField... fields) {
        PackageMovieDealCountryGroupTestData rec = PackageMovieDealCountryGroupTestData.PackageMovieDealCountryGroup(fields);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

}