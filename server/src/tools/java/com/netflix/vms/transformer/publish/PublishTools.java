package com.netflix.vms.transformer.publish;

import com.netflix.gutenberg.publisher.GutenbergFilePublisher;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class PublishTools {

    @Test
    public void publishLogicToTest() throws IOException {
        final String vip = "vmsdev_sunjeetsn";
        File jarFile = new File("/Users/sunjeets/workspace/nInputsToTransformer/vmstransformer/business-logic/build/libs/vmstransformer-business-logic-2.186.0-dev.55.uncommitted+dynamic.deploy.c0fcefc.jar");    // SNAP: path
        GutenbergFilePublisher.localProxyForTestEnvironment().publish("vmstransformer-logic-" + vip, jarFile);
        GutenbergFilePublisher.localProxyForTestEnvironment().publish("vmstransformer-logic-" + vip + "_override", jarFile);
    }

    // @Test
    public void publishLogicToPreProd() throws IOException {
        String description = "Add explicit check for no available package";
        Map<String, String> metadata = Collections.singletonMap("release_description", description);

        // File jarFile = new File("/common/git/gatekeeper2-logic/bin/main/logic.jar");
        // GutenbergFilePublisher.localProxyForProdEnvironment().publish("gatekeeper2.logic.preprod", jarFile, metadata);
    }

    // @Test
    public void publishLogicToProd() throws IOException {
        String description = "hasAnyAudio is a high-bar/low-bar concept";
        String tagInGit = "hasaudio-highbar";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("release_description", description);
        metadata.put("git_tag", tagInGit);

        // File jarFile = new File("/common/git/gatekeeper2-logic/bin/main/logic.jar");
        // GutenbergFilePublisher.localProxyForProdEnvironment().publish("gatekeeper2.logic.prod", jarFile, metadata);
    }

}
