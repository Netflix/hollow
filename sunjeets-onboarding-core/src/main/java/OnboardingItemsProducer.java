import com.google.inject.Inject;
import com.netflix.cinder.producer.CinderProducerBuilder;
import com.netflix.hollow.api.producer.HollowProducer;

public class OnboardingItemsProducer {

    private HollowProducer producer;

    @Inject
    public OnboardingItemsProducer(CinderProducerBuilder.Factory producerBuilder) {
        producer = producerBuilder.get().forNamespace("sunjeets-onboarding").build();
        // To develop locally using local storage:
        // producer = HollowProducerProxyBuilder.localProxyForDevEnvironment("my-unique-namespace", "path-on-local-disk").build();
        producer.initializeDataModel(OnboardingItem.class);
    }

    public void runEveryOnceInAwhile() {
        producer.runCycle(state -> {
            state.add(new OnboardingItem(1, "Software Engineer Bootcamp", true));
            state.add(new OnboardingItem(2, "Cinder demo", false));
        });

    }

}
