package com.netflix.vms.transformer.consumer;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import java.io.File;
import java.util.function.Supplier;

public class VMSInputDataConsumer {

    public static final String TEST_PROXY_URL = "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-test";
    public static final String PROD_PROXY_URL = "http://discovery.cloud.netflix.net:7001/discovery/resolver/cluster/vmshollowloaderblobproxy-vmstools-prod";

    /*
     * Create a HollowConsumer for consuming input data. This is used to update data from converter at the
     * start of {@code TransformCycle}
     */
    public static HollowConsumer getNewConsumer(Supplier<CinderConsumerBuilder> builder, String namespace, Class<? extends HollowAPI> apiClass) {

        return builder.get()
                .forNamespace(namespace)
                .withGeneratedAPIClass(apiClass)
                .noAnnouncementWatcher()
                .build();
    }

    /*
     * Create a HollowConsumer for consuming data via the blob proxy. This is useful in testing.
     */
    public static HollowConsumer getNewProxyConsumer(
            Supplier<CinderConsumerBuilder> builder,
            String namespace,
            String workingDir,
            boolean isProd,
            Class<? extends HollowAPI> apiClass) {

        GutenbergFileConsumer proxyFileConsumer = isProd ?
                GutenbergFileConsumer.localProxyForProdEnvironment() : GutenbergFileConsumer.localProxyForTestEnvironment();

        return builder.get()
            .forNamespace(namespace)
            .withBlobRetriever(new NFHollowBlobRetriever(proxyFileConsumer, namespace))
            .withLocalBlobStore(new File(workingDir))
            .withGeneratedAPIClass(apiClass)
            .noAnnouncementWatcher()
            .build();
    }
}
