package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.InMemoryBlobStore;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.HollowBlobHeader;
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.read.engine.HollowBlobHeaderReader;
import java.io.IOException;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;

public class SchemaChangeTest {

    static class V1 {
        static class A {
            int i;

            A(int i) {
                this.i = i;
            }
        }
    }

    static class V2 {
        static class A {
            int i;
            String s;

            A(int i, String s) {
                this.i = i;
                this.s = s;
            }
        }
    }

    @Test
    public void test() throws Exception {
        InMemoryBlobStore bs = new InMemoryBlobStore();

        HollowProducer producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = producer.runCycle(ws -> {
            ws.add(new V1.A(1));
        });

        testChangeHeader(bs, Long.MIN_VALUE, v1, false);


        producer = HollowProducer.withPublisher(bs)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(V2.A.class);
        producer.restore(v1, bs);

        long v2 = producer.runCycle(ws -> {
            ws.add(new V2.A(1, "1"));
        });

        HollowConsumer.Blob blob = bs.retrieveSnapshotBlob(v2);
        HollowBlobHeaderReader r = new HollowBlobHeaderReader();
        HollowBlobHeader header = r.readHeader(blob.getInputStream());

        testChangeHeader(bs, v1, v2, true);
    }

    void testChangeHeader(HollowConsumer.BlobRetriever br,
            long fromVersion, long toVersion, boolean present) throws IOException {
        testChangeHeader(getHeader(br, r -> r.retrieveSnapshotBlob(toVersion)), present);

        if (fromVersion != Long.MIN_VALUE) {
            testChangeHeader(getHeader(br, r -> r.retrieveDeltaBlob(fromVersion)), present);
        }
    }

    void testChangeHeader(HollowBlobHeader header, boolean present) {
        Assert.assertEquals(
                present,
                header.getHeaderTags().containsKey(HollowStateEngine.HEADER_TAG_SCHEMA_CHANGE));
        if (present) {
            String v = header.getHeaderTags().get(HollowStateEngine.HEADER_TAG_SCHEMA_CHANGE);
            Assert.assertTrue(Boolean.parseBoolean(v));
        }
    }

    HollowBlobHeader getHeader(HollowConsumer.BlobRetriever br,
            Function<HollowConsumer.BlobRetriever, HollowConsumer.Blob> f) throws IOException {
        HollowConsumer.Blob blob = f.apply(br);
        HollowBlobHeaderReader r = new HollowBlobHeaderReader();
        return r.readHeader(blob.getInputStream());
    }
}
