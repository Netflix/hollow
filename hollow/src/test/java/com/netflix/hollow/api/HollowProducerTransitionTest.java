package com.netflix.hollow.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.netflix.hollow.api.producer.HollowProducer;

import com.netflix.hollow.api.producer.HollowProducer.Transition;

public final class HollowProducerTransitionTest {

    HollowProducer.Transition subject;

    @Test
    public void snapshot() {
        subject = new Transition(13L);

        assertThat(subject.getFromVersion(), equalTo(Long.MIN_VALUE));
        assertThat(subject.getToVersion(), equalTo(13L));

        assertFalse(subject.isDiscontinous());
        assertTrue(subject.isSnapshot());
        assertFalse(subject.isDelta());
    }

    @Test
    public void delta() {
        subject = new HollowProducer.Transition(2L, 3L);

        assertThat(subject.getFromVersion(), equalTo(2L));
        assertThat(subject.getToVersion(), equalTo(3L));

        assertFalse(subject.isDiscontinous());
        assertFalse(subject.isSnapshot());
        assertTrue(subject.isDelta());
    }

    @Test
    public void advancing() {
        subject = new Transition(6L);

        assertThat(subject.getFromVersion(), equalTo(Long.MIN_VALUE));
        assertThat(subject.getToVersion(), equalTo(6L));
        assertFalse(subject.isDiscontinous());
        assertFalse(subject.isDelta());

        subject = subject.advance(7L);

        assertThat(subject.getFromVersion(), equalTo(6L));
        assertThat(subject.getToVersion(), equalTo(7L));
        assertFalse(subject.isDiscontinous());
        assertTrue(subject.isDelta());
    }

    @Test
    public void reversing() {
        subject = new Transition(21L, 22L);

        assertTrue(subject.isForwardDelta());
        assertFalse(subject.isReverseDelta());

        subject = subject.reverse();

        assertThat(subject.getFromVersion(), equalTo(22L));
        assertThat(subject.getToVersion(), equalTo(21L));
        assertFalse(subject.isForwardDelta());
        assertTrue(subject.isReverseDelta());

        try {
            new Transition(1L).reverse();
            fail("expected exception");
        } catch(IllegalStateException expected){}
    }
}
