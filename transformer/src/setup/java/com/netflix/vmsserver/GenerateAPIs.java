package com.netflix.vmsserver;

import com.netflix.hollow.HollowSchema;
import com.netflix.hollow.codegen.HollowAPIGenerator;
import com.netflix.hollow.codegen.HollowPOJOGenerator;
import com.netflix.hollow.util.HollowSchemaParser;
import com.netflix.hollow.util.HollowWriteStateCreator;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.hollow.zenoadapter.HollowSerializationFramework;
import com.netflix.videometadata.hollow.VMSObjectHashCodeFinder;
import com.netflix.videometadata.serializer.framework.VMSSerializerFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class GenerateAPIs {

    private static final String TRANSFORMER_PROJECT_BASE_DIR = "/common/git/videometadata-transformer";
    private static final String CONVERTER_PROJECT_BASE_DIR = "/common/git/videometadata-converter";

    @Test
    public void generateEverythingElse() throws IOException {
        String schemas = IOUtils.toString(new FileReader(CONVERTER_PROJECT_BASE_DIR + "/src/main/resources/schemas.txt"));
        Collection<HollowSchema> configuredSchemas = HollowSchemaParser.parseCollectionOfSchemas(schemas);

        HollowWriteStateEngine stateEngine = HollowWriteStateCreator.createWithSchemas(configuredSchemas);

        HollowAPIGenerator videosGenerator = new HollowAPIGenerator("VMSHollowVideoInputAPI", "com.netflix.vms.transformer.hollowinput", stateEngine);
        videosGenerator.generateFiles(TRANSFORMER_PROJECT_BASE_DIR + "/src/main/java/com/netflix/vms/transformer/hollowinput");
    }


    @Test
    public void bootstrapOutputPOJOs() throws IOException {
        HollowSerializationFramework hollowFramework = new HollowSerializationFramework(VMSSerializerFactory.getInstance(), new VMSObjectHashCodeFinder());
        HollowWriteStateEngine stateEngine = hollowFramework.getStateEngine();

        HollowPOJOGenerator generator = new HollowPOJOGenerator("com.netflix.vms.transformer.hollowoutput", stateEngine);

        generator.generateFiles(TRANSFORMER_PROJECT_BASE_DIR + "/src/main/java/com/netflix/vms/transformer/hollowoutput");
    }

}
