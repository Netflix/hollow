package com.netflix.hollow.api.producer;

public interface HollowProducerAccessor {
    HollowProducerListener getProducerMetricsListener(HollowProducer producer);
}
