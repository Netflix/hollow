package com.netflix.vms.transformer.data.gen.award;

import com.netflix.hollow.api.testdata.HollowTestDataset;

public class AwardTestData extends HollowTestDataset {

    public StringTestData String(StringTestData.StringField... fields) {
        StringTestData rec = StringTestData.String(fields);
        add(rec);
        return rec;
    }

    public VMSAwardTestData VMSAward(VMSAwardTestData.VMSAwardField... fields) {
        VMSAwardTestData rec = VMSAwardTestData.VMSAward(fields);
        add(rec);
        return rec;
    }

}