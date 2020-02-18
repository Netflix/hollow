package com.netflix.sunjeetsonboardingroot;

import com.google.inject.Inject;
import com.netflix.cinder.consumer.CinderConsumerBuilder;
import java.util.function.Supplier;

public class AppCinderConsumer {


    private Supplier<CinderConsumerBuilder> consumerBuilderSupplier;

    public Supplier<CinderConsumerBuilder> getCinderConsumerBuilderSupplier() {
        return consumerBuilderSupplier;
    }

    @Inject
    public AppCinderConsumer(Supplier<CinderConsumerBuilder> consumerBuilderSupplier) {
        this.consumerBuilderSupplier = consumerBuilderSupplier;
    }

}
