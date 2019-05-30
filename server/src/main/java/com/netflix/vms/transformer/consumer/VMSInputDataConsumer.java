package com.netflix.vms.transformer.consumer;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import java.util.function.Supplier;

public class VMSInputDataConsumer {

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

    //
    // TODO: FileStore removal
    //
    // public static HollowConsumer getNewProxyConsumer(
    //         String baseProxyURL,
    //         String localDataDir,
    //         String converterVip) {
    //
    //     throw new RuntimeException("Unsupported Operation");
    //
    //     // return builder.get()
    //     //     .forNamespace(converterVip)
    //     //     .withGeneratedAPIClass(VMSHollowInputAPI.class)
    //     //     .withBlobRetriever(new VMSDataProxyTransitionCreator(baseProxyURL, localDataDir, converterVip))
    //     //     .withAnnouncementWatcher(new VMSInputDataProxyUpdateDirector(baseProxyURL, converterVip))
    //     //     .build();
    // }
}
