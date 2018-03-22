/*
 *
 *  Copyright 2018 Netflix, Inc.
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

import com.netflix.hollow.api.producer.validation.HollowValidationListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ListenerSupportTest {
    interface ProducerAndValidationListener
            extends HollowProducerListenerV2, HollowValidationListener {
    }

    private ListenerSupport listenerSupport;

    @Mock
    private HollowProducerListenerV2 listener;
    @Mock
    private HollowValidationListener validationListener;
    @Mock
    private ProducerAndValidationListener producerAndValidationListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        listenerSupport = new ListenerSupport();
        listenerSupport.add(listener);
        listenerSupport.add(validationListener);
        listenerSupport.add((HollowValidationListener) producerAndValidationListener);
        listenerSupport.add((HollowProducerListenerV2) producerAndValidationListener);
    }

    @Test
    public void testFireValidationStart() {
        long version = 31337;
        HollowProducer.ReadState readState = Mockito.mock(HollowProducer.ReadState.class);
        Mockito.when(readState.getVersion()).thenReturn(version);
        listenerSupport.fireValidationStart(readState);
        Mockito.verify(listener).onValidationStart(version);
        Mockito.verify(validationListener).onValidationStart(version);
        Mockito.verify(producerAndValidationListener).onValidationStart(version);
    }
}
