package com.netflix.hollow.diffview;

import static org.junit.Assert.assertTrue;

import com.netflix.hollow.diff.ui.HollowDiffUIServer;
import com.netflix.hollow.tools.diff.HollowDiff;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HollowDiffUITest {

    private static final int PORT = 17777;
    private static final String DIFF_PATH = "diff";

    private HollowDiffUIServer server;

    @Before
    public void init() throws Exception {
        HollowDiff testDiff = new FakeHollowDiffGenerator().createFakeDiff();
        server = new HollowDiffUIServer(PORT);
        server.addDiff(DIFF_PATH, testDiff);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void blobInformationTableIsSortedAlphabetically() throws Exception {
        // FakeHollowDiffGenerator seeds header tags:
        //   from: tag1, tag2, fromTag
        //   to:   tag1, tag2, toTag
        // Alphabetical union: fromTag < tag1 < tag2 < toTag
        String html = fetch("/" + DIFF_PATH + "/");

        int iFromTag = html.indexOf("fromTag");
        int iTag1    = html.indexOf(">tag1<");
        int iTag2    = html.indexOf(">tag2<");
        int iToTag   = html.indexOf("toTag");

        assertTrue("fromTag not found in Blob Information table", iFromTag >= 0);
        assertTrue("tag1 not found in Blob Information table",    iTag1    >= 0);
        assertTrue("tag2 not found in Blob Information table",    iTag2    >= 0);
        assertTrue("toTag not found in Blob Information table",   iToTag   >= 0);

        assertTrue("Expected fromTag before tag1 (alphabetical order)", iFromTag < iTag1);
        assertTrue("Expected tag1 before tag2 (alphabetical order)",    iTag1    < iTag2);
        assertTrue("Expected tag2 before toTag (alphabetical order)",   iTag2    < iToTag);
    }

    private String fetch(String path) throws Exception {
        URL url = new URL("http://localhost:" + PORT + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8").useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
