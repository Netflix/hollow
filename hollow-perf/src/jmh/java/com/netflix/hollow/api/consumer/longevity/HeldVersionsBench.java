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
import com.netflix.hollow.api.consumer.HollowConsumer.HeldVersionsConfig;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * A producer of movies whose every version after the first retitles one percent of them, so each delta removes and adds
 * that many movies and titles, and held-mode consumers of its versions.
 */
final class HeldVersionsBench {
    static final int MOVIES = 100_000;
    static final int CHANGES_PER_DELTA = 1_000;

    private final Path dir;
    private final HollowProducer producer;
    private final Movie[] catalog = new Movie[MOVIES];
    private final Random random = new Random(17);
    private int cycle;

    HeldVersionsBench() throws IOException {
        dir = Files.createTempDirectory("held-versions-bench");
        producer = HollowProducer.withPublisher(new HollowFilesystemPublisher(dir))
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(Integer.MAX_VALUE) // one snapshot, then deltas
                .build();
        for (int id = 0; id < MOVIES; id++)
            catalog[id] = new Movie(id, "title " + id, 1950 + random.nextInt(75));
    }

    /** Publishes {@code count} versions, oldest first. */
    List<Long> cycles(int count) {
        List<Long> versions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (cycle++ > 0) {
                for (int c = 0; c < CHANGES_PER_DELTA; c++) {
                    int index = random.nextInt(MOVIES);
                    Movie movie = catalog[index];
                    catalog[index] = new Movie(movie.id, "title " + movie.id + "@" + cycle, movie.year);
                }
            }
            versions.add(producer.runCycle(state -> {
                for (Movie movie : catalog)
                    state.add(movie);
            }));
        }
        return versions;
    }

    /** A held-mode consumer without a lead limit, reading held versions through direct lookup or the chain. */
    HollowConsumer consumer(boolean directLookup) {
        return HollowConsumer.withBlobRetriever(new HollowFilesystemBlobRetriever(dir))
                .withObjectLongevity(ObjectLongevity.held(new HeldVersionsConfig() {
                    @Override
                    public int maxDeltasPastOldestHold() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public boolean enableDirectLookup() {
                        return directLookup;
                    }
                }))
                .build();
    }

    void delete() {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static final class Movie {
        final long id;
        final String title;
        final int year;

        Movie(long id, String title, int year) {
            this.id = id;
            this.title = title;
            this.year = year;
        }
    }
}
