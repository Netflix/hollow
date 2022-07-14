package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashCodeFinderTest {

    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    @Test
    public void testProduceAndRestore() {
        TestHollowObjectHashCodeFinder hcf = new TestHollowObjectHashCodeFinder();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withHashCodeFinder(hcf)
                .build();

        long v1 = producer.runCycle(ws -> ws.add(new Top(100)));
        HollowMapSchema s = (HollowMapSchema) producer.getWriteEngine().getSchema(
                "MapOfStringUsingHashCodeFinderToStringUsingHashCodeFinder");
        Assert.assertNull(s.getHashKey());
        Assert.assertEquals(100, hcf.i.get());

        HollowProducer restoredProducer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withHashCodeFinder(hcf)
                .build();
        restoredProducer.initializeDataModel(Top.class);
        restoredProducer.restore(v1, blobStore);

        // Cycle on restored producer will fail integrity check if hash code are not the same
        long v2 = restoredProducer.runCycle(ws -> ws.add(new Top(101)));
        s = (HollowMapSchema) restoredProducer.getWriteEngine().getSchema(
                "MapOfStringUsingHashCodeFinderToStringUsingHashCodeFinder");
        Assert.assertNull(s.getHashKey());
        Assert.assertEquals(201, hcf.i.get());
    }

    static class Top {
        final Map<StringUsingHashCodeFinder, StringUsingHashCodeFinder> m;

        Top(int n) {
            this.m = new HashMap<>();
            for(int i = 0; i < n; i++) {
                StringUsingHashCodeFinder s = new StringUsingHashCodeFinder(
                        Integer.toString(i));
                m.put(s, s);
            }
        }
    }

    static class StringUsingHashCodeFinder {
        @HollowInline
        final String s;

        StringUsingHashCodeFinder(String s) {
            this.s = s;
        }
    }

    private static class TestHollowObjectHashCodeFinder implements HollowObjectHashCodeFinder {
        final AtomicInteger i = new AtomicInteger();

        @Override
        public int hashCode(Object objectToHash) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode(String typeName, int ordinal, Object objectToHash) {
            if(typeName.equals("StringUsingHashCodeFinder")) {
                i.incrementAndGet();
                String s = ((StringUsingHashCodeFinder) objectToHash).s;
                return s.hashCode() ^ s.charAt(0);
            } else {
                return ordinal;
            }
        }

        @Override
        public Set<String> getTypesWithDefinedHashCodes() {
            return Collections.singleton("StringUsingHashCodeFinder");
        }

        @Override
        public int hashCode(int ordinal, Object objectToHash) {
            throw new UnsupportedOperationException();
        }
    }
}
