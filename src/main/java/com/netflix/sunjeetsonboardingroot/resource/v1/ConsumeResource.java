package com.netflix.sunjeetsonboardingroot.resource.v1;

import com.google.inject.Inject;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.sunjeetsonboardingroot.AppCinderConsumer;
import com.netflix.sunjeetsonboardingroot.api.OnboardingItem;
import com.netflix.sunjeetsonboardingroot.api.SunjeetsOnboardingAPI;
import com.netflix.sunjeetsonboardingroot.startup.JerseyModule;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(JerseyModule.CONSUME_PATH + "{path: (/.*)?}")
public class ConsumeResource {

    private static final Logger logger = LoggerFactory.getLogger(ConsumeResource.class);
    private static final String DEFAULT_NAMESPACE = "SunjeetsOnboardingItems.v1";

    private AppCinderConsumer onboardingItemsConsumer;

    @Inject
    ConsumeResource(AppCinderConsumer onboardingItemsConsumer) {
        this.onboardingItemsConsumer = onboardingItemsConsumer;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response queryConsume() throws Exception {
        logger.info("SNAP: Start querying");
        try {
            HollowConsumer consumer = this.onboardingItemsConsumer.getCinderConsumerBuilderSupplier()
                    .get()
                    .forNamespace(DEFAULT_NAMESPACE)
                    .withGeneratedAPIClass(SunjeetsOnboardingAPI.class)
                    .buildAsync().join();

            SunjeetsOnboardingAPI api = (SunjeetsOnboardingAPI) consumer.getAPI();

            for (OnboardingItem item : api.getAllOnboardingItem()) {
                logger.info("SNAP: item name= " + item.getOnboardingItemName());
            }
            logger.info("SNAP: Queried.");

            System.out.println("Blob is held in memory: " + DateTime.now());
            Thread.sleep(5000);

        } catch (Exception e) {
            logger.error("SNAP: oops, ", e);
            throw e;
        }
        return Response.ok().build();
    }
}
