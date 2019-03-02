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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * A convenience wrapper around ThreadPoolExecutor. Provides sane defaults to
 * constructor arguments and allows for awaitUninterruptibly().
 *
 */
public class SimultaneousExecutor extends ThreadPoolExecutor {

    private static final String DEFAULT_THREAD_NAME = "hollow-simultaneous-executor";

    private final List<Future<?>> futures = new ArrayList<Future<?>>();

    public SimultaneousExecutor() {
        this(1.0d);
    }

    public SimultaneousExecutor(double threadsPerCpu) {
        this(threadsPerCpu, DEFAULT_THREAD_NAME);
    }

    public SimultaneousExecutor(double threadsPerCpu, String threadName) {
        this((int) ((double) Runtime.getRuntime().availableProcessors() * threadsPerCpu), threadName);
    }

    public SimultaneousExecutor(int numThreads) {
        this(numThreads, DEFAULT_THREAD_NAME);
    }

    public SimultaneousExecutor(int numThreads, final String threadName) {
        super(numThreads, numThreads, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, threadName);
                t.setDaemon(true);
                return t;
            }
        });
    }

    @Override
    public void execute(Runnable command) {
        if(command instanceof RunnableFuture) {
            super.execute(command);
        } else {
            super.execute(newTaskFor(command, Boolean.TRUE));
        }
    }

    /**
     * Awaits completion of all submitted tasks.
     *
     * After this call completes, the thread pool will be shut down.
     */
    public void awaitUninterruptibly() {
        shutdown();
        while (!isTerminated()) {
            try {
                awaitTermination(1, TimeUnit.DAYS);
            } catch (final InterruptedException e) { }
        }
    }

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        final RunnableFuture<T> task = super.newTaskFor(runnable, value);
        futures.add(task);
        return task;
    }

    @Override
    protected final <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
        final RunnableFuture<T> task = super.newTaskFor(callable);
        futures.add(task);
        return task;
    }

    /**
     * Await successful completion of all submitted tasks. Throw exception of the first failed task
     * if 1 or more tasks failed.
     *
     * After this call completes, the thread pool will be shut down.
     *
     * @throws ExecutionException if a computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     */
    public void awaitSuccessfulCompletion() throws InterruptedException, ExecutionException {
        awaitUninterruptibly();
        for (final Future<?> f : futures) {
            f.get();
        }
    }

    /**
     * Await successful completion of all previously submitted tasks.  Throw exception of the first failed task
     * if 1 or more tasks failed.
     *
     * After this call completes, the thread pool will <i>not</i> be shut down and can be reused.
     *
     * @throws ExecutionException if a computation threw an
     * exception
     * @throws InterruptedException if the current thread was interrupted
     * while waiting
     */
    public void awaitSuccessfulCompletionOfCurrentTasks() throws InterruptedException, ExecutionException {
        for(Future<?> f : futures) {
            f.get();
        }

        futures.clear();
    }

}
