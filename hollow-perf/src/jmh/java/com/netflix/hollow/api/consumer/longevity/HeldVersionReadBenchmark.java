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

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Field reads and object creation for random movies of a version: on the state engine, through a hold at its version,
 * and through a hold 1, 5 or 20 deltas behind it, read through direct lookup or the chain of copies. Each delta
 * retitles one percent of the movies, so a hold 20 deltas behind reads about a fifth of them from copies.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class HeldVersionReadBenchmark {
    private static final int DELTAS = 20;

    /** The state engine, a hold at its version, or a hold that many deltas behind it and how it reads them. */
    @Param({"engine", "hold", "1-direct", "5-direct", "20-direct", "1-chain", "5-chain", "20-chain"})
    public String target;

    private HeldVersionsBench bench;
    private HollowObjectTypeDataAccess movies;
    private int idField;
    private int yearField;
    private int[] ordinals;
    private int next;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        LogManager.getLogManager().reset();
        bench = new HeldVersionsBench();
        List<Long> versions = bench.cycles(DELTAS + 1);
        HollowConsumer consumer = bench.consumer(!target.endsWith("-chain"));
        consumer.triggerRefreshTo(versions.get(0));
        consumer.holdVersion(versions.get(0)); // keeps every later delta's copies
        consumer.triggerRefreshTo(versions.get(DELTAS));

        HollowDataAccess dataAccess;
        BitSet populated;
        if (target.equals("engine")) {
            dataAccess = consumer.getStateEngine();
            populated = consumer.getStateEngine().getTypeState("Movie").getPopulatedOrdinals();
        } else {
            int behind = target.equals("hold") ? 0 : Integer.parseInt(target.substring(0, target.indexOf('-')));
            HollowVersionHold hold = consumer.holdVersion(versions.get(DELTAS - behind));
            dataAccess = hold.getDataAccess();
            populated = hold.getPopulatedOrdinals("Movie");
        }
        movies = (HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess("Movie");
        idField = movies.getSchema().getPosition("id");
        yearField = movies.getSchema().getPosition("year");

        int[] all = populated.stream().toArray();
        Random random = new Random(31);
        ordinals = new int[4096];
        for (int i = 0; i < ordinals.length; i++)
            ordinals[i] = all[random.nextInt(all.length)];
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        bench.delete();
    }

    /** Reads two fields of a movie. */
    @Benchmark
    public long readFields() {
        int ordinal = ordinals[next++ & (ordinals.length - 1)];
        return movies.readLong(ordinal, idField) + movies.readInt(ordinal, yearField);
    }

    /** Creates a movie object and reads its fields, following its title's reference. */
    @Benchmark
    public long createObject() {
        GenericHollowObject movie = new GenericHollowObject(movies, ordinals[next++ & (ordinals.length - 1)]);
        return movie.getLong("id") + movie.getInt("year") + movie.getObject("title").getString("value").length();
    }
}
