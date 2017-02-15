package com.netflix.hollow.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public final class StateTransitionTest {

    HollowStateTransition subject;

    @Test
    public void discontinuous(){
        subject = new HollowStateTransition();

        assertThat(subject.getFromVersion(), equalTo(Long.MIN_VALUE));
        assertThat(subject.getToVersion(), equalTo(Long.MIN_VALUE));

        assertTrue(subject.isDiscontinous());
        assertFalse(subject.isSnapshot());
        assertFalse(subject.isDelta());
    }

    @Test
    public void snapshot() {
        subject = new HollowStateTransition(13L);

        assertThat(subject.getFromVersion(), equalTo(Long.MIN_VALUE));
        assertThat(subject.getToVersion(), equalTo(13L));

        assertFalse(subject.isDiscontinous());
        assertTrue(subject.isSnapshot());
        assertFalse(subject.isDelta());
    }

    @Test
    public void delta() {
        subject = new HollowStateTransition(2L, 3L);

        assertThat(subject.getFromVersion(), equalTo(2L));
        assertThat(subject.getToVersion(), equalTo(3L));

        assertFalse(subject.isDiscontinous());
        assertFalse(subject.isSnapshot());
        assertTrue(subject.isDelta());
    }

    @Test
    public void advancing() {
        subject = new HollowStateTransition();

        subject = subject.advance(6L);

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
        subject = new HollowStateTransition(21L, 22L);

        assertTrue(subject.isForwardDelta());
        assertFalse(subject.isReverseDelta());

        subject = subject.reverse();

        assertThat(subject.getFromVersion(), equalTo(22L));
        assertThat(subject.getToVersion(), equalTo(21L));
        assertFalse(subject.isForwardDelta());
        assertTrue(subject.isReverseDelta());

        try {
            new HollowStateTransition().reverse();
            fail("expected exception");
        } catch(IllegalStateException expected){}

        try {
            new HollowStateTransition(1L).reverse();
            fail("expected exception");
        } catch(IllegalStateException expected){}
    }
}
