package com.netflix.vms.transformer.common.input;

import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.codegen.HollowTestDataAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import java.io.IOException;

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


    public static void main(String args[]) throws IOException {

        final String ROOT_PROJECT_PATH = System.getProperty("user.dir");    // Points to project root if run via IDE
        final String DATASET = "gatekeeper2";                   // used as-is in package name and file system path
        final String GENERATED_CLASSES_PREFIX = "Gk2Status";    // used as-is as prefix for generated classes
        final String NAMESPACE = "gatekeeper2_status_test";     // Gutenberg pulls in the latest blob for this namespace

        GutenbergFileConsumer gutenberg = GutenbergFileConsumer.localProxyForTestEnvironment();
        generateArtifactsFromData(gutenberg,
                NAMESPACE,
                DATASET,
                GENERATED_CLASSES_PREFIX,
                ROOT_PROJECT_PATH);
    }

    public static void generateArtifactsFromData(GutenbergFileConsumer gutenberg,
            String namespace,
            String dataset,
            String generatedClassesPrefix,
            String rootProjectPath) throws IOException {

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(new NFHollowBlobRetriever(gutenberg, namespace)).build();
        consumer.triggerRefresh();

        new HollowAPIGenerator.Builder()
                .withAPIClassname(generatedClassesPrefix + "API")
                .withPackageName(ROOT_API_PACKAGE + dataset)
                .withErgonomicShortcuts()
                .withDataModel(consumer.getStateEngine())
                .withDestination(rootProjectPath + GENERATED_API_DEST + "/" + dataset)
                .build()
                .generateSourceFiles();

        HollowTestDataAPIGenerator.generate(consumer.getStateEngine(),
                ROOT_TEST_DATA_PACKAGE + dataset,
                generatedClassesPrefix + "TestData",
                rootProjectPath + GENERATED_TEST_DATA_DEST + "/" + dataset);

    }
}
