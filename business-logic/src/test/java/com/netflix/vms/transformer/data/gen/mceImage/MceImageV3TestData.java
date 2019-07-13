package com.netflix.vms.transformer.data.gen.mceImage;

import com.netflix.hollow.api.testdata.HollowTestDataset;

public class MceImageV3TestData extends HollowTestDataset {

    public DerivativeTagTestData DerivativeTag(DerivativeTagTestData.DerivativeTagField... fields) {
        DerivativeTagTestData rec = DerivativeTagTestData.DerivativeTag(fields);
        add(rec);
        return rec;
    }

    public IPLArtworkDerivativeTestData IPLArtworkDerivative(IPLArtworkDerivativeTestData.IPLArtworkDerivativeField... fields) {
        IPLArtworkDerivativeTestData rec = IPLArtworkDerivativeTestData.IPLArtworkDerivative(fields);
        add(rec);
        return rec;
    }

    public IPLArtworkDerivativeSetTestData IPLArtworkDerivativeSet(IPLArtworkDerivativeSetTestData.IPLArtworkDerivativeSetField... fields) {
        IPLArtworkDerivativeSetTestData rec = IPLArtworkDerivativeSetTestData.IPLArtworkDerivativeSet(fields);
        add(rec);
        return rec;
    }

    public IPLDerivativeGroupTestData IPLDerivativeGroup(IPLDerivativeGroupTestData.IPLDerivativeGroupField... fields) {
        IPLDerivativeGroupTestData rec = IPLDerivativeGroupTestData.IPLDerivativeGroup(fields);
        add(rec);
        return rec;
    }

    public IPLDerivativeGroupSetTestData IPLDerivativeGroupSet(IPLDerivativeGroupTestData... elements) {
        IPLDerivativeGroupSetTestData rec = IPLDerivativeGroupSetTestData.IPLDerivativeGroupSet(elements);
        add(rec);
        return rec;
    }

    public IPLDerivativeSetTestData IPLDerivativeSet(IPLArtworkDerivativeTestData... elements) {
        IPLDerivativeSetTestData rec = IPLDerivativeSetTestData.IPLDerivativeSet(elements);
        add(rec);
        return rec;
    }

    public ListOfDerivativeTagTestData ListOfDerivativeTag(DerivativeTagTestData... elements) {
        ListOfDerivativeTagTestData rec = ListOfDerivativeTagTestData.ListOfDerivativeTag(elements);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

}