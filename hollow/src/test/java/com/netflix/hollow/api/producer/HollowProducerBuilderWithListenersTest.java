package com.netflix.hollow.api.producer;

import static org.junit.Assert.assertSame;

import com.netflix.hollow.api.producer.HollowProducer.PublishArtifact;
import com.netflix.hollow.api.producer.metrics.AbstractProducerMetricsListener;
import com.netflix.hollow.api.producer.metrics.AnnouncementMetrics;
import com.netflix.hollow.api.producer.metrics.CycleMetrics;
import java.io.IOException;
import org.junit.Test;

public class HollowProducerBuilderWithListenersTest {

    private static final class NoopPublisher implements HollowProducer.Publisher {
        @Override
        public void publish(PublishArtifact publishArtifact) {}
    }

    private static final class TestMetricsListener extends AbstractProducerMetricsListener {
        @Override
        public void cycleMetricsReporting(CycleMetrics cycleMetrics) {}

        @Override
        public void announcementMetricsReporting(AnnouncementMetrics announcementMetrics) {}
    }

    @Test
    public void withListenersFromExisting_reusesMetricsListenerWhenNotExplicitlySet() throws IOException {
        TestMetricsListener metricsListener = new TestMetricsListener();

        HollowProducer producer1 = HollowProducer.withPublisher(new NoopPublisher())
                .withListener(metricsListener)
                .build();

        assertSame(metricsListener, producer1.getProducerMetricsListener());

        HollowProducer producer2 = HollowProducer.withPublisher(new NoopPublisher())
                .withListenersFromExisting(producer1)
                .build();

        assertSame(producer1.getProducerMetricsListener(), producer2.getProducerMetricsListener());
    }

    @Test
    public void withListenersFromExisting_doesNotOverrideProvidedMetricsListener() throws IOException {
        TestMetricsListener metricsListener = new TestMetricsListener();
        HollowProducer producer1 = HollowProducer.withPublisher(new NoopPublisher())
                .withListener(metricsListener)
                .build();

        TestMetricsListener metricsListener2 = new TestMetricsListener();
        HollowProducer producer2 = HollowProducer.withPublisher(new NoopPublisher())
                .withListener(metricsListener2)
                .withListenersFromExisting(producer1)
                .build();

        assertSame(metricsListener2, producer2.getProducerMetricsListener());
    }
}

