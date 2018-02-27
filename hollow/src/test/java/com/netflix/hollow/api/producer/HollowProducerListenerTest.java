package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.producer.enforcer.SingleProducerEnforcer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

/**
 * Unit tests to verify that HollowProducerListener objects provided to HollowProducers
 * are invoked at the right times.
 */
public class HollowProducerListenerTest {
    private HollowProducer producer;

    @Mock
    private HollowProducerListener listener;
    @Mock
    private HollowProducerListenerV2 listenerV2;
    @Mock
    private SingleProducerEnforcer singleProducerEnforcer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.producer = new HollowProducer.Builder()
            .withListeners(listener, listenerV2)
            .withSingleProducerEnforcer(singleProducerEnforcer).build();
    }

    @Test
    public void testCycleSkip() {
        Mockito.when(singleProducerEnforcer.isPrimary()).thenReturn(false);
        this.producer.runCycle(null);
        Mockito.verify(listenerV2).onCycleSkip(
                HollowProducerListenerV2.CycleSkipReason.NOT_PRIMARY_PRODUCER);
        Mockito.verify(listener, Mockito.never()).onCycleStart(
                ArgumentMatchers.anyLong());
        Mockito.verify(listenerV2, Mockito.never()).onCycleStart(
                ArgumentMatchers.anyLong());
    }

    @Test
    public void testCycleStartEnd() {
        Mockito.when(singleProducerEnforcer.isPrimary()).thenReturn(true);
        this.producer.runCycle(Mockito.mock(HollowProducer.Populator.class));
        Mockito.verify(listener).onCycleStart(ArgumentMatchers.anyLong());
        Mockito.verify(listenerV2).onCycleStart(ArgumentMatchers.anyLong());

        Mockito.verify(listener).onCycleComplete(
                Mockito.any(HollowProducerListener.ProducerStatus.class),
                ArgumentMatchers.anyLong(), Mockito.any(TimeUnit.class));
        Mockito.verify(listenerV2).onCycleComplete(
                Mockito.any(HollowProducerListener.ProducerStatus.class),
                ArgumentMatchers.anyLong(), Mockito.any(TimeUnit.class));
    }
}
