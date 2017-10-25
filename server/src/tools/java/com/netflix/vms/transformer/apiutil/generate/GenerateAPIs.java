package com.netflix.vms.transformer.apiutil.generate;

import static java.lang.System.getenv;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaParser;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class GenerateAPIs {

    private static final String WORKSPACE = getenv().containsKey("VMSWORKSPACE") ? getenv("VMSWORKSPACE") : "/Users/jatins/projects/stash";

    private static final File workspaceFile(String...parts) throws IOException {
        return new File(WORKSPACE, String.join(File.separator, Arrays.asList(parts))).getCanonicalFile();
    }

    @Test
    public void generateInputAPI() throws IOException {
        String schemas = IOUtils.toString(new FileReader(workspaceFile("vmsconverter", "src/main/resources/schemas.txt")));
        Collection<HollowSchema> configuredSchemas = HollowSchemaParser.parseCollectionOfSchemas(schemas);

        HollowWriteStateEngine stateEngine = HollowWriteStateCreator.createWithSchemas(configuredSchemas);

        HollowAPIGenerator videosGenerator = new HollowAPIGenerator("VMSHollowInputAPI", "com.netflix.vms.transformer.hollowinput", stateEngine);

        File outputFolder = workspaceFile("vmstransformer",  "business-logic/src/main/java/com/netflix/vms/transformer/hollowinput");
        cleanupFolder(outputFolder);
        videosGenerator.generateFiles(outputFolder);
    }

    /*@Test
    public void bootstrapOutputPOJOs() throws IOException {
        HollowSerializationFramework hollowFramework = new HollowSerializationFramework(VMSSerializerFactory.getInstance(), new VMSTransformerHashCodeFinder());
        HollowWriteStateEngine stateEngine = hollowFramework.getStateEngine();

        HollowPOJOGenerator generator = new HollowPOJOGenerator("com.netflix.vms.transformer.hollowoutput", stateEngine);

        File outputFolder = workspaceFile("vmstransformer", "src/main/java/com/netflix/vms/transformer/hollowoutput");
        cleanupFolder(outputFolder);
        generator.generateFiles(outputFolder);
    }*/

    private void cleanupFolder(File folder) {
        System.out.println("Cleaning up folder: " + folder.getAbsolutePath());
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                System.out.println("\t deleting file:" + file.getName());
                file.delete();
            }
        }
    }
}
