package com.netflix.vms.transformer.data.gen.cupToken;

import com.netflix.hollow.api.testdata.HollowTestDataset;

public class CupTokenTestData extends HollowTestDataset {

    public CinderCupTokenRecordTestData CinderCupTokenRecord(CinderCupTokenRecordTestData.CinderCupTokenRecordField... fields) {
        CinderCupTokenRecordTestData rec = CinderCupTokenRecordTestData.CinderCupTokenRecord(fields);
        add(rec);
        return rec;
    }

    public LongTestData Long(LongTestData.LongField... fields) {
        LongTestData rec = LongTestData.Long(fields);
        add(rec);
        return rec;
    }

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

}