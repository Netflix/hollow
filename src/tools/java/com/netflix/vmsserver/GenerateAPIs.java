package com.netflix.vmsserver;

import com.netflix.hollow.codegen.HollowAPIGenerator;
import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

public class GenerateAPIs {

    @Test
    public void generatePackages() throws IOException {
        HollowReadStateEngine packagesStateEngine = loadStateEngine("/space/hollowinput/VMSInputPackagesData.hollow");
        HollowAPIGenerator packagesGenerator = new HollowAPIGenerator("VMSHollowPackagesInputAPI", "com.netflix.vms.packages.hollowinput", packagesStateEngine);
        packagesGenerator.generateFiles("/common/git/videometadata/server/test-tools/com/netflix/vms/packages/hollowinput");
    }

    @Test
    public void generateEverythingElse() throws IOException {
        HollowReadStateEngine videosStateEngine = loadStateEngine("/space/hollowinput/VMSInputVideosData.hollow");
        HollowAPIGenerator videosGenerator = new HollowAPIGenerator("VMSHollowVideoInputAPI", "com.netflix.vms.videos.hollowinput", videosStateEngine);
        videosGenerator.generateFiles("/common/git/videometadata/server/test-tools/com/netflix/vms/videos/hollowinput");
    }

    private HollowReadStateEngine loadStateEngine(String snapshotFilename) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();

        HollowBlobReader reader = new HollowBlobReader(stateEngine);

        reader.readSnapshot(new BufferedInputStream(new FileInputStream(snapshotFilename)));

        return stateEngine;

    }

    /*@Test
    public void bootstrapOutputPOJOs() throws IOException {
        HollowSerializationFramework hollowFramework = new HollowSerializationFramework(VMSSerializerFactory.getInstance(), new VMSObjectHashCodeFinder());
        HollowWriteStateEngine stateEngine = hollowFramework.getStateEngine();

        HollowPOJOGenerator generator = new HollowPOJOGenerator("com.netflix.vms.hollowoutput.pojos", stateEngine);

        generator.generateFiles("/common/git/videometadata/server/test-tools/com/netflix/vms/hollowoutput/pojos");
    }*/

}
