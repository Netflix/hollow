package com.netflix.vms.transformer.data.gen.supplemental;

import com.netflix.hollow.api.testdata.HollowTestDataset;

public class SupplementalTestData extends HollowTestDataset {

    public IndividualSupplementalTestData IndividualSupplemental(IndividualSupplementalTestData.IndividualSupplementalField... fields) {
        IndividualSupplementalTestData rec = IndividualSupplementalTestData.IndividualSupplemental(fields);
        add(rec);
        return rec;
    }

    public IndividualSupplementalIdentifierSetTestData IndividualSupplementalIdentifierSet(StringTestData... elements) {
        IndividualSupplementalIdentifierSetTestData rec = IndividualSupplementalIdentifierSetTestData.IndividualSupplementalIdentifierSet(elements);
        add(rec);
        return rec;
    }

    public IndividualSupplementalThemeSetTestData IndividualSupplementalThemeSet(StringTestData... elements) {
        IndividualSupplementalThemeSetTestData rec = IndividualSupplementalThemeSetTestData.IndividualSupplementalThemeSet(elements);
        add(rec);
        return rec;
    }

    public IndividualSupplementalUsageSetTestData IndividualSupplementalUsageSet(StringTestData... elements) {
        IndividualSupplementalUsageSetTestData rec = IndividualSupplementalUsageSetTestData.IndividualSupplementalUsageSet(elements);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

    public SupplementalsTestData Supplementals(SupplementalsTestData.SupplementalsField... fields) {
        SupplementalsTestData rec = SupplementalsTestData.Supplementals(fields);
        add(rec);
        return rec;
    }

    public SupplementalsListTestData SupplementalsList(IndividualSupplementalTestData... elements) {
        SupplementalsListTestData rec = SupplementalsListTestData.SupplementalsList(elements);
        add(rec);
        return rec;
    }

}