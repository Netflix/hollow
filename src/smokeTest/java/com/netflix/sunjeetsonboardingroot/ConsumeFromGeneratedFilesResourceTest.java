package com.netflix.sunjeetsonboardingroot;

import static com.netflix.sunjeetsonboardingroot.resource.v1.ConsumeAndGenerateResource.TEST_FILE_TOPN;

import com.netflix.sunjeetsonboardingroot.resource.v1.ConsumeFromGeneratedFilesResource;
import java.io.IOException;
import org.junit.Test;

public class ConsumeFromGeneratedFilesResourceTest {

    @Test
    public void testReferenceReadState() throws IOException {

        ConsumeFromGeneratedFilesResource.referenceReadState(TEST_FILE_TOPN);
    }
}
