package com.netflix.sunjeetsonboardingroot;

import com.google.inject.Inject;
import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.sunjeetsonboardingroot.OnboardingItem;

public class OnboardingItemsProducer {

    private static final String DEFAULT_NAMESPACE = "SunjeetsOnboardingItems.v1";
    private HollowProducer producer;


    @Inject
    public OnboardingItemsProducer(CinderProducerBuilder.Factory producerBuilder) {
        producer = producerBuilder.get()
                .forNamespace(DEFAULT_NAMESPACE)
                .withRestore()
                .withSingleProducerEnforcer()
                .build();

        producer.initializeDataModel(OnboardingItem.class);

        // To develop locally using local storage:
        // producer = HollowProducerProxyBuilder.localProxyForDevEnvironment("my-unique-namespace", "path-on-local-disk").build();
    }


    public void publishData(boolean isPrimaryProducer) {

        producer.enablePrimaryProducer(isPrimaryProducer);

        if (isPrimaryProducer) {

            producer.runCycle(state -> {
                state.add(new OnboardingItem(1, "Software Engineer Bootcamp", true));
                state.add(new OnboardingItem(2, "Cinder demo", false));
            });

        }

    }

}
