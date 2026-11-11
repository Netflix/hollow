/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.consumer.longevity;

import static com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevityConfig.DEFAULT_CONFIG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.client.HollowAPIFactory;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.HeldVersionsConfig;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity.Mode;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevityConfig;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.util.DefaultHashCodeFinder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class ObjectLongevityModesTest {

    private final HeldVersionsFixture fixture = new HeldVersionsFixture();

    @Test
    public void factoriesCarryTheirModeAndConfig() {
        ObjectLongevityConfig expiringConfig = longLived(new AtomicBoolean(true));
        HeldVersionsConfig heldConfig = new HeldVersionsConfig() {};

        assertEquals(Mode.OFF, ObjectLongevity.off().getMode());
        assertSame(DEFAULT_CONFIG, ObjectLongevity.off().getObjectLongevityConfig());
        assertNull(ObjectLongevity.off().getHeldVersionsConfig());

        assertEquals(Mode.EXPIRING, ObjectLongevity.expiring(expiringConfig).getMode());
        assertSame(expiringConfig, ObjectLongevity.expiring(expiringConfig).getObjectLongevityConfig());
        assertNull(ObjectLongevity.expiring(expiringConfig).getHeldVersionsConfig());

        assertEquals(Mode.HELD, ObjectLongevity.held(heldConfig).getMode());
        assertSame(DEFAULT_CONFIG, ObjectLongevity.held(heldConfig).getObjectLongevityConfig());
        assertSame(heldConfig, ObjectLongevity.held(heldConfig).getHeldVersionsConfig());
        assertSame(HeldVersionsConfig.DEFAULT, ObjectLongevity.held().getHeldVersionsConfig());

        try {
            ObjectLongevity.expiring(null);
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            ObjectLongevity.held(null);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void offIsTheDefault() {
        long v1 = fixture.cycle();
        HollowConsumer consumer = fixture.consumer().build();
        consumer.triggerRefreshTo(v1);

        assertSame(consumer.getStateEngine(), consumer.getAPI().getDataAccess());
        assertHoldThrows(consumer, v1);
    }

    @Test
    public void withObjectLongevityConfigIsExpiringAndBehavesAsToday() {
        List<Long> versions = fixture.cycles(4);
        AtomicBoolean longLived = new AtomicBoolean();
        ObjectLongevityConfig config = longLived(longLived);

        for (HollowConsumer consumer : Arrays.asList(
                fixture.consumer().withObjectLongevityConfig(config).build(),
                fixture.consumer().withObjectLongevity(ObjectLongevity.expiring(config)).build())) {
            longLived.set(true);
            consumer.triggerRefreshTo(versions.get(0));
            HollowAPI first = consumer.getAPI();
            consumer.triggerRefreshTo(versions.get(1));

            assertTrue(first.getDataAccess() instanceof HollowProxyDataAccess);
            fixture.assertReads(versions.get(0), first.getDataAccess());
            assertHoldThrows(consumer, versions.get(1));

            longLived.set(false);
            consumer.triggerRefreshTo(versions.get(2));
            HollowAPI third = consumer.getAPI();
            assertSame(consumer.getStateEngine(), third.getDataAccess());
            consumer.triggerRefreshTo(versions.get(3));
            fixture.assertReads(versions.get(3), third.getDataAccess());

            longLived.set(true);
            consumer.triggerRefreshTo(fixture.cycle());
            assertTrue(consumer.getAPI().getDataAccess() instanceof HollowProxyDataAccess);
        }
    }

    @Test
    public void theLastBuilderCallChoosesTheMode() {
        long v1 = fixture.cycle();
        ObjectLongevityConfig config = longLived(new AtomicBoolean(true));

        HollowConsumer expiring = fixture.consumer()
                .withObjectLongevity(ObjectLongevity.held())
                .withObjectLongevityConfig(config)
                .build();
        expiring.triggerRefreshTo(v1);
        assertTrue(expiring.getAPI().getDataAccess() instanceof HollowProxyDataAccess);
        assertHoldThrows(expiring, v1);

        HollowConsumer held = fixture.consumer()
                .withObjectLongevityConfig(config)
                .withObjectLongevity(ObjectLongevity.held())
                .build();
        held.triggerRefreshTo(v1);
        assertSame(held.getStateEngine(), held.getAPI().getDataAccess());
        assertEquals(v1, held.holdVersion(v1).getVersion());

        HollowConsumer off = fixture.consumer()
                .withObjectLongevity(ObjectLongevity.held())
                .withObjectLongevity(ObjectLongevity.off())
                .build();
        off.triggerRefreshTo(v1);
        assertHoldThrows(off, v1);
    }

    @Test
    public void builderFieldKeepsMeaningTheExpiringConfig() {
        long v1 = fixture.cycle();
        ObjectLongevityConfig config = longLived(new AtomicBoolean(true));

        assertSame(DEFAULT_CONFIG, new FieldBuilder().field());
        assertSame(DEFAULT_CONFIG, new FieldBuilder().withObjectLongevity(ObjectLongevity.held()).field());
        assertSame(config, new FieldBuilder().withObjectLongevity(ObjectLongevity.expiring(config)).field());
        assertSame(config, new FieldBuilder().withObjectLongevityConfig(config).field());
        assertSame(DEFAULT_CONFIG, new FieldBuilder()
                .withObjectLongevityConfig(config)
                .withObjectLongevity(ObjectLongevity.off()).field());

        // a subclass that writes the field directly chooses expiring mode, as it did before modes existed
        HollowConsumer consumer = new FieldBuilder()
                .withObjectLongevity(ObjectLongevity.held())
                .field(config)
                .withBlobRetriever(fixture.blobStore)
                .build();
        consumer.triggerRefreshTo(v1);
        assertTrue(consumer.getAPI().getDataAccess() instanceof HollowProxyDataAccess);
        assertHoldThrows(consumer, v1);
    }

    @Test
    public void deprecatedConstructorsMeanExpiring() {
        long v1 = fixture.cycle();
        HollowConsumer consumer = new DeprecatedConsumer(fixture, longLived(new AtomicBoolean(true)));
        consumer.triggerRefreshTo(v1);

        assertTrue(consumer.getAPI().getDataAccess() instanceof HollowProxyDataAccess);
        assertHoldThrows(consumer, v1);
    }

    @Test
    public void heldModeApiIsTheEnginesOwn() {
        List<Long> versions = fixture.cycles(3);
        HollowConsumer consumer = fixture.heldConsumer();
        consumer.triggerRefreshTo(versions.get(0));
        HollowAPI api = consumer.getAPI();
        HollowVersionHold hold = consumer.holdVersion(versions.get(0));

        consumer.triggerRefreshTo(versions.get(1));
        consumer.triggerRefreshTo(versions.get(2));

        assertSame(api, consumer.getAPI());
        assertSame(consumer.getStateEngine(), api.getDataAccess());
        fixture.assertReads(versions.get(2), api.getDataAccess());
        fixture.assertReads(versions.get(0), hold.getDataAccess());
    }

    private static void assertHoldThrows(HollowConsumer consumer, long version) {
        try {
            consumer.holdVersion(version);
            fail("holdVersion outside held mode");
        } catch (IllegalStateException expected) {
        }
    }

    private static ObjectLongevityConfig longLived(AtomicBoolean enabled) {
        return new ObjectLongevityConfig() {
            @Override
            public boolean enableLongLivedObjectSupport() {
                return enabled.get();
            }

            @Override
            public boolean enableExpiredUsageStackTraces() {
                return false;
            }

            @Override
            public long gracePeriodMillis() {
                return 60_000L;
            }

            @Override
            public long usageDetectionPeriodMillis() {
                return 60_000L;
            }

            @Override
            public boolean dropDataAutomatically() {
                return false;
            }

            @Override
            public boolean forceDropData() {
                return false;
            }
        };
    }

    private static final class FieldBuilder extends HollowConsumer.Builder<FieldBuilder> {
        ObjectLongevityConfig field() {
            return objectLongevityConfig;
        }

        FieldBuilder field(ObjectLongevityConfig config) {
            objectLongevityConfig = config;
            return this;
        }
    }

    @SuppressWarnings("deprecation")
    private static final class DeprecatedConsumer extends HollowConsumer {
        DeprecatedConsumer(HeldVersionsFixture fixture, ObjectLongevityConfig config) {
            super(fixture.blobStore, null, Collections.<RefreshListener>emptyList(), HollowAPIFactory.DEFAULT_FACTORY,
                    null, config, ObjectLongevityDetector.DEFAULT_DETECTOR, DoubleSnapshotConfig.DEFAULT_CONFIG,
                    new DefaultHashCodeFinder(), Runnable::run, MemoryMode.ON_HEAP);
        }
    }
}
