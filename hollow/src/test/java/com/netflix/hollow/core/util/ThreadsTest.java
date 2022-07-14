package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.util.Threads.daemonThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

// TODO(timt): tag as MEDIUM test
public class ThreadsTest {
    @Test
    public void named() {
        Thread thread = daemonThread(() -> {
        }, "Thready McThreadson");

        assertEquals("Thready McThreadson", thread.getName());
        assertTrue(thread.isDaemon()); // TODO(timt): invariant
    }

    @Test
    public void described() {
        Thread thread = daemonThread(() -> {
        }, getClass(), "howdy");

        assertEquals("hollow | ThreadsTest | howdy", thread.getName());
        assertTrue(thread.isDaemon());
    }

    @Test
    public void described_customPlatform() {
        Thread thread = daemonThread(() -> {
        }, "solid", getClass(), "howdy");

        assertEquals("solid | ThreadsTest | howdy", thread.getName());
        assertTrue(thread.isDaemon());
    }

    @Test
    public void nullName() {
        try {
            daemonThread(() -> {
            }, null);
            fail("expected an exception");
        } catch (NullPointerException e) {
            assertEquals("name required", e.getMessage());
        }
    }

    @Test
    public void nullPlatform() {
        try {
            daemonThread(() -> {
            }, null, getClass(), "boom");
            fail("expected an exception");
        } catch (NullPointerException e) {
            assertEquals("platform required", e.getMessage());
        }
    }

    @Test
    public void nullRunnable() {
        try {
            daemonThread(null, getClass(), "boom");
            fail("expected an exception");
        } catch (NullPointerException e) {
            assertEquals("runnable required", e.getMessage());
        }
    }

    @Test
    public void nullContext() {
        try {
            daemonThread(() -> {
            }, null, "boom");
            fail("expected an exception");
        } catch (NullPointerException e) {
            assertEquals("context required", e.getMessage());
        }
    }

    @Test
    public void nullDescription() {
        try {
            daemonThread(() -> {
            }, getClass(), null);
            fail("expected an exception");
        } catch (NullPointerException e) {
            assertEquals("description required", e.getMessage());
        }
    }
}
