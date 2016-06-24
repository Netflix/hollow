package com.netflix.vms.transformer.apiutil.generate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.netflix.hollow.HollowSchema;
import com.netflix.hollow.codegen.HollowAPIGenerator;
import com.netflix.hollow.codegen.HollowPOJOGenerator;
import com.netflix.hollow.util.HollowSchemaParser;
import com.netflix.hollow.util.HollowWriteStateCreator;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.zenoadapter.HollowSerializationFramework;
import com.netflix.videometadata.serializer.framework.VMSSerializerFactory;
import com.netflix.vms.transformer.util.VMSTransformerHashCodeFinder;

public class GenerateAPIs {

    private static final String TRANSFORMER_PROJECT_BASE_DIR = "/common/git/vmstransformer/business-logic";
    private static final String CONVERTER_PROJECT_BASE_DIR = "/common/git/vmsconverter";

    @Test
    public void generateInputAPI() throws IOException {
        String schemas = IOUtils.toString(new FileReader(CONVERTER_PROJECT_BASE_DIR + "/src/main/resources/schemas.txt"));
        Collection<HollowSchema> configuredSchemas = HollowSchemaParser.parseCollectionOfSchemas(schemas);

        HollowWriteStateEngine stateEngine = HollowWriteStateCreator.createWithSchemas(configuredSchemas);

        HollowAPIGenerator videosGenerator = new HollowAPIGenerator("VMSHollowInputAPI", "com.netflix.vms.transformer.hollowinput", stateEngine);

        String outputFolder = TRANSFORMER_PROJECT_BASE_DIR + "/business-logic/src/main/java/com/netflix/vms/transformer/hollowinput";
        cleanupFolder(outputFolder);
        videosGenerator.generateFiles(outputFolder);
    }


    @Test
    public void bootstrapOutputPOJOs() throws IOException {
        HollowSerializationFramework hollowFramework = new HollowSerializationFramework(VMSSerializerFactory.getInstance(), new VMSTransformerHashCodeFinder());
        HollowWriteStateEngine stateEngine = hollowFramework.getStateEngine();

        HollowPOJOGenerator generator = new HollowPOJOGenerator("com.netflix.vms.transformer.hollowoutput", stateEngine);

        String outputFolder = TRANSFORMER_PROJECT_BASE_DIR + "/src/main/java/com/netflix/vms/transformer/hollowoutput";
        cleanupFolder(outputFolder);
        generator.generateFiles(outputFolder);
    }

    private void cleanupFolder(String folder) {
        System.out.println("Cleaning up folder: " + folder);
        File dir = new File(folder);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                System.out.println("\t deleting file:" + file.getName());
                file.delete();
            }
        }
    }

}
