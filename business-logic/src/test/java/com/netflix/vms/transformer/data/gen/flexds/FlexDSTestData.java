package com.netflix.vms.transformer.data.gen.flexds;

import com.netflix.hollow.api.testdata.HollowTestDataMapEntry;
import com.netflix.hollow.api.testdata.HollowTestDataset;

public class FlexDSTestData extends HollowTestDataset {

    public AuditGroupTestData AuditGroup(AuditGroupTestData.AuditGroupField... fields) {
        AuditGroupTestData rec = AuditGroupTestData.AuditGroup(fields);
        add(rec);
        return rec;
    }

    public ContainerTestData Container(ContainerTestData.ContainerField... fields) {
        ContainerTestData rec = ContainerTestData.Container(fields);
        add(rec);
        return rec;
    }

    public DisplaySetTestData DisplaySet(DisplaySetTestData.DisplaySetField... fields) {
        DisplaySetTestData rec = DisplaySetTestData.DisplaySet(fields);
        add(rec);
        return rec;
    }

    public ListOfStringTestData ListOfString(StringTestData... elements) {
        ListOfStringTestData rec = ListOfStringTestData.ListOfString(elements);
        add(rec);
        return rec;
    }

    public SetOfContainerTestData SetOfContainer(ContainerTestData... elements) {
        SetOfContainerTestData rec = SetOfContainerTestData.SetOfContainer(elements);
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

}