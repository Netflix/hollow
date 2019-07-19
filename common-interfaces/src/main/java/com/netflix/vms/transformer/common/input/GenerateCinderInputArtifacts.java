package com.netflix.vms.transformer.common.input;

import com.google.common.collect.ImmutableList;
import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowTestDataAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import java.io.IOException;
import java.util.List;

/**
 * This class generates API and test data classes for a Cinder namespace, by pulling in the latest data from the namespace.
 * Since type names across namespaces might repeat, generated artifacts should be configured to be placed under
 * namespace-specific java packages.
 */
public class GenerateCinderInputArtifacts {

    private static final String ROOT_API_PACKAGE = "com.netflix.vms.transformer.input.api.gen.";
    private static final String ROOT_TEST_DATA_PACKAGE = "com.netflix.vms.transformer.data.gen.";
    private static final String GENERATED_API_DEST = "/business-logic/src/main/java/com/netflix/vms/transformer/input/api/gen";
    private static final String GENERATED_TEST_DATA_DEST = "/business-logic/src/test/java/com/netflix/vms/transformer/data/gen";

    private static List<ArtifactsSpec> ARTIFACT_SPECS = ImmutableList.<ArtifactsSpec>builder()
            //.add(new ArtifactsSpec("gatekeeper2_status_test","gatekeeper2","Gk2Status"))  // GK2
            .add(new ArtifactsSpec("oscar_entityfeed_testintg","oscar","Oscar"))          // Oscar
            //.add(new ArtifactsSpec("flexds_testintg","flexds","FlexDS"))                  // FlexDS
            .build();

    public static class ArtifactsSpec {
        String namespace;         // Gutenberg pulls in the latest blob for this namespace
        String dataset;           // used as-is in package name and file system path
        String prefix;            // used as-is as prefix for generated classes

        public ArtifactsSpec(String namespace, String dataset, String prefix) {
            this.namespace = namespace;
            this.dataset = dataset;
            this.prefix = prefix;
        }
    }

    public static void main(String args[]) throws IOException {

        final String ROOT_PROJECT_PATH = System.getProperty("user.dir");    // Points to project root if run via IDE

        GutenbergFileConsumer gutenberg = GutenbergFileConsumer.localProxyForTestEnvironment();
        for (ArtifactsSpec spec : ARTIFACT_SPECS) {
            generateArtifactsFromData(gutenberg,
                    spec,
                    ROOT_PROJECT_PATH);
        }
    }

    public static void generateArtifactsFromData(GutenbergFileConsumer gutenberg,
            ArtifactsSpec spec,
            String rootProjectPath) throws IOException {

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(new NFHollowBlobRetriever(gutenberg, spec.namespace)).build();
        consumer.triggerRefresh();

        new HollowAPIGenerator.Builder()
                .withAPIClassname(spec.prefix + "API")
                .withPackageName(ROOT_API_PACKAGE + spec.dataset)
                .withErgonomicShortcuts()
                .withDataModel(consumer.getStateEngine())
                .withDestination(rootProjectPath + GENERATED_API_DEST + "/" + spec.dataset)
                .build()
                .generateSourceFiles();

        HollowTestDataAPIGenerator.generate(consumer.getStateEngine(),
                ROOT_TEST_DATA_PACKAGE + spec.dataset,
                spec.prefix + "TestData",
                rootProjectPath + GENERATED_TEST_DATA_DEST + "/" + spec.dataset);

    }
}
