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
package com.netflix.hollow.core.util;

import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import org.junit.Before;
import org.junit.Test;

// TODO(timt): tag as MEDIUM test
public class SimultaneousExecutorTest {

    private SimultaneousExecutor subject;

    @Before
    public void before() {
        subject = new SimultaneousExecutor(getClass(), "test");
    }

    @Test
    public void failsWhenAnyRunnableThrowsException() throws Exception {
        subject.execute(new Job(false));
        subject.execute(new Job(false));
        subject.execute(new Job(true));
        subject.execute(new Job(false));

        try {
            subject.awaitSuccessfulCompletion();
            fail("Should have thrown Exception");
        } catch (Exception expected) {
        }
    }

    @Test
    public void failsWhenAnyCallableThrowsException() throws Exception {
        StatusEnsuringCallable firstTask = new StatusEnsuringCallable(false);
        StatusEnsuringCallable secondTask = new StatusEnsuringCallable(false);

        subject.submit(firstTask);
        subject.submit(secondTask);

        try {
            subject.awaitSuccessfulCompletion();
            fail("Should fail");
        } catch (final Exception e) {
        }
    }

    @Test
    public void canBeReused() throws Exception {
        subject.execute(new Job(false));
        subject.execute(new Job(false));
        subject.execute(new Job(false));
        subject.execute(new Job(false));

        subject.awaitSuccessfulCompletionOfCurrentTasks();

        subject.execute(new Job(false));
        subject.execute(new Job(false));
        subject.execute(new Job(false));
        subject.execute(new Job(false));

        subject.awaitSuccessfulCompletion();
    }

    private class Job implements Runnable {

        private final boolean fail;

        public Job(boolean fail) {
            this.fail = fail;
        }

        public void run() {
            if(fail)
                throw new RuntimeException("FAIL");
        }

    }

    private static class StatusEnsuringCallable implements Callable<Void> {
        private final boolean shouldSucceed;

        public StatusEnsuringCallable(final boolean shouldSucceed) {
            this.shouldSucceed = shouldSucceed;

        }

        @Override
        public Void call() throws Exception {
            if(shouldSucceed) {
                return null;
            } else {
                throw new RuntimeException("Failing as configured");
            }
        }
    }

}
