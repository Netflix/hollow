package com.netflix.hollow.diffview;

import com.netflix.hollow.diffview.effigy.HollowRecordDiffUI;
import com.netflix.hollow.history.ui.HollowHistoryUI;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.history.HollowHistory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class HollowEffigyFieldPairerTest {

    @Test
    public void testTheSameObjects() throws IOException {
        HollowDiff diff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowRecordDiffUI diffUI = new HollowHistoryUI("", (HollowHistory) null);
        HollowObjectDiffViewGenerator diffGenerator = new HollowObjectDiffViewGenerator(diff.getFromStateEngine(), diff.getToStateEngine(), diffUI, "TypeA", 0, 0);

        HollowDiffViewRow diffViewRows = diffGenerator.getHollowDiffViewRows();
        Assert.assertFalse(diffViewRows.getFieldPair().isDiff());
    }

    @Test
    public void testFromEmpty() throws IOException {
        HollowDiff diff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowRecordDiffUI diffUI = new HollowHistoryUI("", (HollowHistory) null);
        HollowObjectDiffViewGenerator diffGenerator = new HollowObjectDiffViewGenerator(diff.getFromStateEngine(), diff.getToStateEngine(), diffUI, "TypeA", -1, 0);

        HollowDiffViewRow diffViewRows = diffGenerator.getHollowDiffViewRows();
        Assert.assertTrue(diffViewRows.getFieldPair().isDiff());
    }

    @Test
    public void testToEmpty() throws IOException {
        HollowDiff diff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowRecordDiffUI diffUI = new HollowHistoryUI("", (HollowHistory) null);
        HollowObjectDiffViewGenerator diffGenerator = new HollowObjectDiffViewGenerator(diff.getFromStateEngine(), diff.getToStateEngine(), diffUI, "TypeA", 0, -1);

        HollowDiffViewRow diffViewRows = diffGenerator.getHollowDiffViewRows();
        Assert.assertTrue(diffViewRows.getFieldPair().isDiff());
    }

    @Test
    public void testBothEmpty() throws IOException {
        HollowDiff diff = new FakeHollowDiffGenerator().createFakeDiff();

        HollowRecordDiffUI diffUI = new HollowHistoryUI("", (HollowHistory) null);
        HollowObjectDiffViewGenerator diffGenerator = new HollowObjectDiffViewGenerator(diff.getFromStateEngine(), diff.getToStateEngine(), diffUI, "TypeA", -1, -1);

        HollowDiffViewRow diffViewRows = diffGenerator.getHollowDiffViewRows();
        Assert.assertFalse(diffViewRows.getFieldPair().isDiff());
    }


}