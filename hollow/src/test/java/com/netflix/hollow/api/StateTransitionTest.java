package com.netflix.hollow.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public final class StateTransitionTest {

    StateTransition subject;

    @Test
    public void discontinuous(){
        subject = new StateTransition();

        assertThat(subject.getFromVersion(), equalTo(Long.MIN_VALUE));
        assertThat(subject.getToVersion(), equalTo(Long.MIN_VALUE));

        assertTrue(subject.isDiscontinous());
        assertFalse(subject.isDelta());
    }

    @Test
    public void resumed() {
        subject = new StateTransition(13L);

        assertThat(subject.getFromVersion(), equalTo(Long.MIN_VALUE));
        assertThat(subject.getToVersion(), equalTo(13L));

        assertFalse(subject.isDiscontinous());
        assertFalse(subject.isDelta());
    }

    @Test
    public void bidirectional() {
        subject = new StateTransition(2L, 3L);

        assertThat(subject.getFromVersion(), equalTo(2L));
        assertThat(subject.getToVersion(), equalTo(3L));

        assertFalse(subject.isDiscontinous());
        assertTrue(subject.isDelta());
    }

    @Test
    public void advancing() {
        subject = new StateTransition();

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

}
