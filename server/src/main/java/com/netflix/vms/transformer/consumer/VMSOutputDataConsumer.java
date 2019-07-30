package com.netflix.vms.transformer.consumer;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.cinder.consumer.NFHollowBlobRetriever;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer;
import java.util.function.Supplier;

public class VMSOutputDataConsumer {

    /*
     * Create a HollowConsumer for consuming output data.
     */
    public static HollowConsumer getNewConsumer(Supplier<CinderConsumerBuilder> builder, String namespace) {

        return builder.get()
                .forNamespace(namespace)
                .noAnnouncementWatcher()
                .build();
    }
}
