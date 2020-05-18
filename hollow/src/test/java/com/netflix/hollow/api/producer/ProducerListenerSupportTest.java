/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.producer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ProducerListenerSupportTest {
    interface ProducerAndValidationStatusListener
            extends HollowProducerListener, ValidationStatusListener {
    }

    private ProducerListenerSupport listenerSupport;

    @Mock
    private HollowProducerListener listener;
    @Mock
    private ValidationStatusListener validationStatusListener;
    @Mock
    private ProducerAndValidationStatusListener producerAndValidationStatusListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        listenerSupport = new ProducerListenerSupport();
        listenerSupport.addListener(listener);
        listenerSupport.addListener(validationStatusListener);
        listenerSupport.addListener(producerAndValidationStatusListener);
    }

    @Test
    public void testDuplicates() {
        ProducerListenerSupport ls = new ProducerListenerSupport();
        CycleListener l = Mockito.mock(CycleListener.class);
        ls.addListener(l);
        ls.addListener(l);

        ProducerListenerSupport.ProducerListeners s = ls.listeners();
        s.fireCycleStart(1);

        Mockito.verify(l, Mockito.times(1)).onCycleStart(1);
    }

    @Test
    public void testAddDuringCycle() {
        ProducerListenerSupport ls = new ProducerListenerSupport();

        class SecondCycleListener implements CycleListener {
            int cycleStart;
            int cycleComplete;

            @Override public void onCycleSkip(CycleSkipReason reason) {
            }

            @Override public void onNewDeltaChain(long version) {
            }

            @Override public void onCycleStart(long version) {
                cycleStart++;
            }

            @Override public void onCycleComplete(Status status, HollowProducer.ReadState rs, long version, Duration elapsed) {
                cycleComplete++;
            }
        }

        class FirstCycleListener extends SecondCycleListener {
            private SecondCycleListener scl = new SecondCycleListener();

            @Override public void onCycleStart(long version) {
                super.onCycleStart(version);
                ls.addListener(scl);
            }
        }

        FirstCycleListener fcl = new FirstCycleListener();
        ls.addListener(fcl);

        ProducerListenerSupport.ProducerListeners s = ls.listeners();
        s.fireCycleStart(1);
        s.fireCycleComplete(new Status.StageWithStateBuilder());

        Assert.assertEquals(1, fcl.cycleStart);
        Assert.assertEquals(1, fcl.cycleComplete);
        Assert.assertEquals(0, fcl.scl.cycleStart);
        Assert.assertEquals(0, fcl.scl.cycleComplete);

        s = ls.listeners();
        s.fireCycleStart(1);
        s.fireCycleComplete(new Status.StageWithStateBuilder());

        Assert.assertEquals(2, fcl.cycleStart);
        Assert.assertEquals(2, fcl.cycleComplete);
        Assert.assertEquals(1, fcl.scl.cycleStart);
        Assert.assertEquals(1, fcl.scl.cycleComplete);
    }

    @Test
    public void testRemoveDuringCycle() {
        ProducerListenerSupport ls = new ProducerListenerSupport();

        class SecondCycleListener implements CycleListener {
            int cycleStart;
            int cycleComplete;

            @Override public void onCycleSkip(CycleSkipReason reason) {
            }

            @Override public void onNewDeltaChain(long version) {
            }

            @Override public void onCycleStart(long version) {
                cycleStart++;
            }

            @Override public void onCycleComplete(Status status, HollowProducer.ReadState rs, long version, Duration elapsed) {
                cycleComplete++;
            }
        }

        class FirstCycleListener extends SecondCycleListener {
            private SecondCycleListener scl;

            private FirstCycleListener(SecondCycleListener scl) {
                this.scl = scl;
            }

            @Override public void onCycleStart(long version) {
                super.onCycleStart(version);
                ls.removeListener(scl);
            }
        }

        SecondCycleListener scl = new SecondCycleListener();
        FirstCycleListener fcl = new FirstCycleListener(scl);
        ls.addListener(fcl);
        ls.addListener(scl);

        ProducerListenerSupport.ProducerListeners s = ls.listeners();
        s.fireCycleStart(1);
        s.fireCycleComplete(new Status.StageWithStateBuilder());

        Assert.assertEquals(1, fcl.cycleStart);
        Assert.assertEquals(1, fcl.cycleComplete);
        Assert.assertEquals(1, fcl.scl.cycleStart);
        Assert.assertEquals(1, fcl.scl.cycleComplete);

        s = ls.listeners();
        s.fireCycleStart(1);
        s.fireCycleComplete(new Status.StageWithStateBuilder());

        Assert.assertEquals(2, fcl.cycleStart);
        Assert.assertEquals(2, fcl.cycleComplete);
        Assert.assertEquals(1, fcl.scl.cycleStart);
        Assert.assertEquals(1, fcl.scl.cycleComplete);
    }

    @Test
    public void testFireValidationStart() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        listenerSupport.listeners().fireValidationStart(readState);
        Mockito.verify(listener).onValidationStart(version);
        Mockito.verify(validationStatusListener).onValidationStatusStart(version);
        Mockito.verify(producerAndValidationStatusListener).onValidationStart(version);
    }

    @Test
    public void testFireValidationStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(validationStatusListener).onValidationStatusStart(version);
        listenerSupport.listeners().fireValidationStart(readState);
        Mockito.verify(listener).onValidationStart(version);
        Mockito.verify(validationStatusListener).onValidationStatusStart(version);
        Mockito.verify(producerAndValidationStatusListener).onValidationStart(version);
    }

    @Test
    public void testFireValidationStartDontStopWhenOneFails2() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);

        Mockito.doThrow(RuntimeException.class).when(validationStatusListener).onValidationStatusStart(version);
        listenerSupport.listeners().fireValidationStart(readState);
        Mockito.verify(listener).onValidationStart(version);
        Mockito.verify(validationStatusListener).onValidationStatusStart(version);
        Mockito.verify(producerAndValidationStatusListener).onValidationStart(version);
    }

    @Test
    public void fireProducerInitDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onProducerInit(1L, MILLISECONDS);
        listenerSupport.listeners().fireProducerInit(1L);
        Mockito.verify(listener).onProducerInit(Duration.ofMillis(1L));
    }

    @Test
    public void fireProducerRestoreStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onProducerRestoreStart(version);
        Status.RestoreStageBuilder b = new Status.RestoreStageBuilder();
        listenerSupport.listeners().fireProducerRestoreComplete(b);
        ArgumentCaptor<Status> status = ArgumentCaptor.forClass(
                Status.class);
        ArgumentCaptor<Long> desired = ArgumentCaptor.forClass(
                long.class);
        ArgumentCaptor<Long> reached = ArgumentCaptor.forClass(
                long.class);
        ArgumentCaptor<Duration> elapsed = ArgumentCaptor.forClass(
                Duration.class);
        Mockito.verify(listener).onProducerRestoreComplete(status.capture(), desired.capture(), reached.capture(), elapsed.capture());
        Assert.assertNotNull(status.getValue());
        Assert.assertNotNull(elapsed.getValue());
    }

    @Test
    public void fireNewDeltaChainDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onNewDeltaChain(version);
        listenerSupport.listeners().fireNewDeltaChain(version);
        Mockito.verify(listener).onNewDeltaChain(version);

    }

    @Test
    public void fireCycleStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onCycleStart(version);
        listenerSupport.listeners().fireCycleStart(version);
        Mockito.verify(listener).onCycleStart(version);
    }

    @Test
    public void firePopulateStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onPopulateStart(version);
        listenerSupport.listeners().firePopulateStart(version);
        Mockito.verify(listener).onPopulateStart(version);
    }

    @Test
    public void firePublishStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onPublishStart(version);
        listenerSupport.listeners().firePublishStart(version);
        Mockito.verify(listener).onPublishStart(version);
    }

    @Test
    public void fireIntegrityCheckStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onIntegrityCheckStart(version);
        listenerSupport.listeners().fireIntegrityCheckStart(readState);
        Mockito.verify(listener).onIntegrityCheckStart(version);
    }


    @Test
    public void fireAnnouncementStartDontStopWhenOneFails() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        Mockito.doThrow(RuntimeException.class).when(listener).onAnnouncementStart(version);
        listenerSupport.listeners().fireAnnouncementStart(readState);
        Mockito.verify(listener).onAnnouncementStart(version);
    }
}
