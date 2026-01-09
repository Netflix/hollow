package com.netflix.hollow.api.producer;

final class HollowProducerAccessHelper {
    static {
        HollowProducer.setAccessor(new HollowProducerAccessor() {
            @Override
            public HollowProducerListener getProducerMetricsListener(HollowProducer producer) {
                return producer.getProducerMetricsListener();
            }
        });
    }

    // force class to load (builder will call this)
    static void ensureInit() {}
}
