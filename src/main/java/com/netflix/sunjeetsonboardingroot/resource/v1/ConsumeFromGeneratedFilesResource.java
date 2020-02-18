package com.netflix.sunjeetsonboardingroot.resource.v1;

import static com.netflix.sunjeetsonboardingroot.resource.v1.ConsumeAndGenerateResource.TEST_FILE_TOPN;

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.explorer.ui.jetty.HollowExplorerUIServer;
import com.netflix.sunjeetsonboardingroot.generated.topn.TopNAPI;
import com.netflix.sunjeetsonboardingroot.startup.JerseyModule;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.BitSet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(JerseyModule.CONSUME_FROM_GENERATED_PATH + "{path: (/.*)?}")
public class ConsumeFromGeneratedFilesResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response queryConsume() throws Exception {

        HollowReadStateEngine readState = referenceReadState(TEST_FILE_TOPN);

        // Explore
        HollowExplorerUIServer uiServer = new HollowExplorerUIServer(readState, 7777);
        uiServer.start();
        uiServer.join();

        return Response.ok().build();
    }

    public static HollowReadStateEngine referenceReadState(String file) throws IOException {
        HollowReadStateEngine readState = new HollowReadStateEngine(true);
        HollowBlobReader reader = new HollowBlobReader(readState);
        reader.readSnapshot(new BufferedInputStream(new FileInputStream(file)));

        TopNAPI topNAPI = new TopNAPI(readState);
        topNAPI.getAllTopN();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readState.getTypeState("TopN");

        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        PrimaryKey pkey = typeState.getSchema().getPrimaryKey();
        int numOrdinals = populatedOrdinals.cardinality();
        System.out.println("Ordinal cardinality= " + numOrdinals);

        int ordinal = populatedOrdinals.nextSetBit(0);
        int count = 0;
        while (ordinal != -1 && count < 1) {
            GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);
            System.out.println("ordinal= " + ordinal);

            long id = obj.getLong("videoId");

            System.out.println("id= " + id);
            System.out.println("obj= " + obj);

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
            count ++;
        }

        return readState;
    }
}
