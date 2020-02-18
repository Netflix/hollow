package com.netflix.sunjeetsonboardingroot.resource.v1;

import com.google.inject.Inject;
import com.netflix.sunjeetsonboardingroot.OnboardingItemsProducer;
import com.netflix.sunjeetsonboardingroot.startup.JerseyModule;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(JerseyModule.PRODUCE_ONCE_PATH + "{path: (/.*)?}")
public class ProduceResource {

    private static final Logger logger = LoggerFactory.getLogger(ProduceResource.class);
    private OnboardingItemsProducer onboardingItemsProducer;

    @Inject
    ProduceResource(OnboardingItemsProducer onboardingItemsProducer) {
        this.onboardingItemsProducer = onboardingItemsProducer;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response publishOnce() throws Exception {
        logger.info("SNAP: Start publishing once");

        this.onboardingItemsProducer.publishData(true);

        logger.info("SNAP: Published.");
        return Response.ok().build();
    }

    /*
    public static void publishEndlessly() throws Exception {
        logger.info("SNAP: Start publishing endlessly");

        while (true) {
            Thread.sleep(10*1000);
            System.out.println("SNAP: Publishing,");
            logger.info("SNAP: Publishing,");

            onboardingItemsProducer.publishData(true);

            System.out.println("SNAP: Published.");
            logger.info("SNAP: Published.");
        }
    }*/
}
