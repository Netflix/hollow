package com.netflix.hollow.api.producer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// this test doesn't do much beyond making sure that a custom builder will
// compile and ensure that HollowProducer.Builder is parameterized correctly
// to allow custom builder methods to be interleaved with base class builder
// methods
public class CustomProducerBuilderTest {

    @Before
    public void setUp() {
    }

    @Test
    public void defaultBehavior() {
        HollowProducer producer = new AugmentedBuilder()
                .build();
        Assert.assertFalse(producer instanceof AugmentedProducer);
    }

    @Test
    public void augmentedBehavior() {
        HollowProducer consumer = new AugmentedBuilder()
                .withNumStatesBetweenSnapshots(42) // should be called before custom builder methods
                .withAugmentation()
                .build();
        Assert.assertTrue(consumer instanceof AugmentedProducer);
    }

    private static class AugmentedBuilder extends HollowProducer.Builder<AugmentedBuilder> {
        private boolean shouldAugment = false;
        AugmentedBuilder withAugmentation() {
            shouldAugment = true;
            return this;
        }

        @Override
        public HollowProducer build() {
            checkArguments();
            HollowProducer producer;
            if(shouldAugment)
                producer = new AugmentedProducer(null, null);
            else
                producer = super.build();
            return producer;
        }
    }

    private static class AugmentedProducer extends HollowProducer {
        AugmentedProducer(
                HollowProducer.Publisher publisher,
                HollowProducer.Announcer announcer
        ) {
            super(publisher, announcer);        }

        @Override
        public String toString() {
            return "I am augmented";
        }
    }
}
