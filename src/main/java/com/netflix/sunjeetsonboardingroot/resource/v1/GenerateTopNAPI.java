package com.netflix.sunjeetsonboardingroot.resource.v1;

import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.SimpleHollowDataset;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateTopNAPI {
    
    public static void main(String args[]) throws IOException {
        generateAPIs(GutenbergFileConsumer.localProxyForTestEnvironment(),
                     "vms.popularViewables.topN",
                     "com.netflix.sunjeetsonboardingroot.generated.topn",
                     "TopNAPI",
                     "/Users/sunjeets/workspace/onboarding/sunjeets-onboarding-root/src/main/java/com/netflix/sunjeetsonboardingroot/generated/topn");

    }

    public static void generateAPIs(GutenbergFileConsumer gutenberg,
            String namespace,
            String packageName,
            String apiClassname,
            String destinationPath) throws IOException {

        HollowDataset dataModel = new SimpleHollowDataset(getSchemasForNamespace(gutenberg, namespace));

        generateAPIs(dataModel, packageName, apiClassname, destinationPath);
    }

    private static List<HollowSchema> getSchemasForNamespace(GutenbergFileConsumer gutenberg, String namespace) throws IOException {
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(new NFHollowBlobRetriever(gutenberg, namespace)).build();

        consumer.triggerRefresh();

        return consumer.getStateEngine().getSchemas();
    }

    private static void generateAPIs(HollowDataset dataModel,
            String packageName,
            String apiClassname,
            String destinationPath) throws IOException {

        new HollowAPIGenerator.Builder()
                .withAPIClassname(apiClassname)
                .withPackageName(packageName)
                .withErgonomicShortcuts()
                .withDataModel(dataModel)
                .withDestination(destinationPath)
                .build()
                .generateSourceFiles();

    }
}
