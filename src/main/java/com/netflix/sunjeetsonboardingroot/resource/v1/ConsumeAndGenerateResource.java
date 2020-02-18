package com.netflix.sunjeetsonboardingroot.resource.v1;

import com.google.inject.Inject;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.util.HollowWriteStateCreator;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.sunjeetsonboardingroot.AppCinderConsumer;
import com.netflix.sunjeetsonboardingroot.generated.topn.TopNAPI;
import com.netflix.sunjeetsonboardingroot.startup.JerseyModule;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(JerseyModule.CONSUME_AND_GENERATE_PATH + "{path: (/.*)?}")
public class ConsumeAndGenerateResource {

    public static final String TEST_NAMESPACE = "vms.popularViewables.topN";
    public static final String TEST_FILE_TOPN = "/Users/sunjeets/workspace/onboarding/topN";
    private static final Logger logger = LoggerFactory.getLogger(ConsumeAndGenerateResource.class);

    private AppCinderConsumer cinderConsumer;

    @Inject
    ConsumeAndGenerateResource(AppCinderConsumer cinderConsumer) {
        this.cinderConsumer = cinderConsumer;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response queryConsume() throws Exception {
        logger.info("SNAP: Start querying");
        //HollowFilterConfig hfc = new HollowFilterConfig();
        //hfc.addType("Actor");
        HollowConsumer consumer;
        try {
            consumer = this.cinderConsumer.getCinderConsumerBuilderSupplier()
                    .get()
                    .forNamespace(TEST_NAMESPACE)
                    .withGeneratedAPIClass(TopNAPI.class)
                    //.withFilterConfig(hfc)
                    .buildAsync().join();

            // TopNAPI api = (TopNAPI) consumer.getAPI();
            // for (TopN topN : api.getAllTopN()) {
            //     logger.info("SNAP: video id= " + topN.getVideoId());
            // }
            // logger.info("SNAP: Queried.");

            // while(true) {
            // System.out.println("Blob is held in memory: " + DateTime.now());
            // Thread.sleep(5000);
            // }

        } catch (Exception e) {
            logger.error("SNAP: oops, ", e);
            throw e;
        }

        HollowWriteStateEngine writeState = HollowWriteStateCreator.recreateAndPopulateUsingReadEngine(consumer.getStateEngine());

        // Convert write state to read state:
        // HollowReadStateEngine readState = new HollowReadStateEngine();
        // StateEngineRoundTripper.roundTripSnapshot(writeState, readState);
        // HollowExplorerUIServer uiServer = new HollowExplorerUIServer(readState, 7777);
        // uiServer.start();
        // uiServer.join();

        HollowBlobWriter writer = new HollowBlobWriter(writeState);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(TEST_FILE_TOPN));
        writer.writeSnapshot(os);
        os.flush();

        return Response.ok().build();
    }
}
