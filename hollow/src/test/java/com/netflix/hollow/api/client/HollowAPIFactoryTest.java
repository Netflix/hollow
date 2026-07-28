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
package com.netflix.hollow.api.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.client.HollowAPIFactory.ForGeneratedAPI;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class HollowAPIFactoryTest {

    @Test
    public void createAPI_withRetainRemovedOrdinals_usesFiveArgConstructorAndPassesTrue() {
        ForGeneratedAPI<RetentionAwareAPI> factory =
                new ForGeneratedAPI<>(RetentionAwareAPI.class, true, "TypeA");

        RetentionAwareAPI api = factory.createAPI(null, null);

        assertEquals(5, api.constructorArgCount);
        assertTrue(api.retainRemovedOrdinalsReceived);
    }

    @Test
    public void createAPI_withoutRetainRemovedOrdinals_usesFourArgConstructor() {
        ForGeneratedAPI<RetentionAwareAPI> factory =
                new ForGeneratedAPI<>(RetentionAwareAPI.class, false, "TypeA");

        RetentionAwareAPI api = factory.createAPI(null, null);

        assertEquals(4, api.constructorArgCount);
        assertFalse(api.retainRemovedOrdinalsReceived);
    }

    @Test
    public void createAPI_firstCycleWithoutPrevious_ignoresRetainFlag() {
        // the initial snapshot load has no previous api to retain from, so it must use the standard
        // 2-arg constructor and never attempt (or require) the retain-aware constructor
        ForGeneratedAPI<RetentionAwareAPI> factory =
                new ForGeneratedAPI<>(RetentionAwareAPI.class, true, "TypeA");

        RetentionAwareAPI api = factory.createAPI(null);

        assertEquals(2, api.constructorArgCount);
        assertFalse(api.retainRemovedOrdinalsReceived);
    }

    @Test
    public void createAPI_whenRetainRequestedButApiWasGeneratedWithoutSupport_throwsClearError() {
        ForGeneratedAPI<LegacyAPI> factory =
                new ForGeneratedAPI<>(LegacyAPI.class, true, "TypeA");

        try {
            factory.createAPI(null, null);
            fail("expected IllegalStateException because the generated API has no retain-aware constructor");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("retainRemovedOrdinals"));
        }
    }

    /** Stub standing in for a generated API produced with the new codegen (has the 5-arg constructor). */
    public static class RetentionAwareAPI extends HollowAPI {
        final int constructorArgCount;
        final boolean retainRemovedOrdinalsReceived;

        public RetentionAwareAPI(HollowDataAccess dataAccess) {
            super(dataAccess);
            this.constructorArgCount = 1;
            this.retainRemovedOrdinalsReceived = false;
        }

        public RetentionAwareAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
            super(dataAccess);
            this.constructorArgCount = 2;
            this.retainRemovedOrdinalsReceived = false;
        }

        public RetentionAwareAPI(HollowDataAccess dataAccess, Set<String> cachedTypes,
                Map<String, Object> factoryOverrides, RetentionAwareAPI previousCycleAPI) {
            super(dataAccess);
            this.constructorArgCount = 4;
            this.retainRemovedOrdinalsReceived = false;
        }

        public RetentionAwareAPI(HollowDataAccess dataAccess, Set<String> cachedTypes,
                Map<String, Object> factoryOverrides, RetentionAwareAPI previousCycleAPI,
                boolean retainRemovedOrdinals) {
            super(dataAccess);
            this.constructorArgCount = 5;
            this.retainRemovedOrdinalsReceived = retainRemovedOrdinals;
        }
    }

    /** Stub standing in for an API produced with the old codegen (no retain-aware constructor). */
    public static class LegacyAPI extends HollowAPI {
        public LegacyAPI(HollowDataAccess dataAccess) {
            super(dataAccess);
        }

        public LegacyAPI(HollowDataAccess dataAccess, Set<String> cachedTypes) {
            super(dataAccess);
        }

        public LegacyAPI(HollowDataAccess dataAccess, Set<String> cachedTypes,
                Map<String, Object> factoryOverrides, LegacyAPI previousCycleAPI) {
            super(dataAccess);
        }
    }
}
