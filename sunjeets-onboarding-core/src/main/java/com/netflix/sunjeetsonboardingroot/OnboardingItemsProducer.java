package com.netflix.sunjeetsonboardingroot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.hollow.api.producer.HollowProducer;
import java.util.HashSet;
import java.util.function.Supplier;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OnboardingItemsProducer {

    private static final String DEFAULT_NAMESPACE = "SunjeetsOnboardingItems.v1";
    private HollowProducer producer;

    private static final Logger logger = LoggerFactory.getLogger(OnboardingItemsProducer.class);

    @Inject
    public OnboardingItemsProducer(Supplier<CinderProducerBuilder> producerBuilderSupplier) {

        logger.info("SNAP: Injecting OnboardingItemsProducer instance");

        producer = producerBuilderSupplier.get()
                .forNamespace(DEFAULT_NAMESPACE)
                .withRestore()
                .withSingleProducerEnforcer()
                .build();

        producer.initializeDataModel(OnboardingItem.class);
        producer.initializeDataModel(CustomString.class);
        producer.initializeDataModel(RawString.class);
        producer.initializeDataModel(Movie.class);
        producer.initializeDataModel(Actor.class);
        producer.initializeDataModel(FooType.class);
        // producer.initializeDataModel(BarType.class);

        // To develop locally using local storage:
        // producer = HollowProducerProxyBuilder.localProxyForDevEnvironment("my-unique-namespace", "path-on-local-disk").build();
    }


    public void publishData(boolean isPrimaryProducer) {

        // producer.enablePrimaryProducer(isPrimaryProducer);
        producer.enablePrimaryProducer(true);
        try {

            producer.runCycle(state -> {
                state.getStateEngine().addHeaderTag("testKey2", "testValueCinderHeaderTag2");
                state.add(new OnboardingItem(1, "Test run at " + DateTime.now(), true));
                state.add(new OnboardingItem(2, "Cinder Hello World", true));
                state.add(new OnboardingItem(3, "Coldstarts with Tim", true));
                state.add(new OnboardingItem(4, "History of VMS with Lavanya", true));
                state.add(new OnboardingItem(5, "Eventprocessing with Tim", true));
                state.add(new OnboardingItem(6, "Future of VMS with Lavanya", true));
                state.add(new OnboardingItem(7, "VMS data model with David", true));
                state.add(new OnboardingItem(8, "Gutenberg with Kinesh", true));
                state.add(new OnboardingItem(9, "ULog with David", true));
                state.add(new OnboardingItem(10, "Oncall stuff with Jatin", false));
                state.add(new OnboardingItem(11, "Hollow metrics revamp", true));
                state.add(new OnboardingItem(12, "Cinder metrics revamp", true));
                state.add(new OnboardingItem(13, "Cinder release", true));

                state.add(new RawString(new HashSet<String>() {{
                    add("AA");
                    add("BB");
                    add("CC");
                    add("DD");
                }}));

                state.add(new CustomString(new HashSet<WrappedString>() {{
                    add(new WrappedString("ZZ"));
                    add(new WrappedString("YY"));
                    add(new WrappedString("XX"));
                    add(new WrappedString("WW"));
                }}));

                state.add(new Movie(1, "First movie ever"));
                state.add(new Movie(2, "Second movie ever"));
                state.add(new Movie(3, "Third movie ever"));
                state.add(new Movie(4, "Fourth movie ever"));
//
                state.add(new Actor(1, "First actor ever"));
                state.add(new Actor(2, "Second actor ever"));
//
                state.add(new FooType(123));
//
                // state.add(new BarType(456));
                // state.add(new BarType(789));
            });
        } finally {
            producer.enablePrimaryProducer(false);
        }

    }

}
