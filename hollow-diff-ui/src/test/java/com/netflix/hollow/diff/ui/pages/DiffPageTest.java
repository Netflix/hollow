package com.netflix.hollow.diff.ui.pages;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;

public class DiffPageTest {

    @Test
    public void testUriFromGitShaTag() {
        Map<String, String> testHeaders = new HashMap<String, String>() {{
            put("businesslogic.stash.VMS.vmstransformer", "7cddd8c555214bfdbc8efc0781ad8610c0b45f94");
        }};

        Assert.assertEquals(
                "https://stash.corp.netflix.com/projects/VMS/repos/vmstransformer/commits/7cddd8c555214bfdbc8efc0781ad8610c0b45f94",
                DiffPage.uriFromGitShaTag(testHeaders));


        Assert.assertEquals(null, DiffPage.uriFromGitShaTag(MapUtils.EMPTY_MAP));
        Assert.assertEquals(null, DiffPage.uriFromGitShaTag(null));
    }
}
