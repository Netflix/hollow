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
package com.netflix.hollow;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemAnnouncer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// Note: this class should always remain in `hollow` module (i.e. `hollow.jar` as it's used to determine JAR version at runtime
public final class Hollow {
    @HollowPrimaryKey(fields="id")
    public static class Movie {
        int id;

        public Movie(int id) {
            this.id = id;
        }
    }
    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("/Users/stevenewald/testhol");

        HollowFilesystemPublisher publisher = new HollowFilesystemPublisher(dir);
        HollowFilesystemAnnouncer announcer = new HollowFilesystemAnnouncer(dir);
        HollowProducer producer = HollowProducer
                .withPublisher(publisher)
                .withAnnouncer(announcer)
                .build();

        System.out.println("Adding...");
        producer.runCycle(state -> {
            for(int i = 0; i < 550_000_000; i++) {
                if(i%2_000_000==0) {
                    System.out.println((float)i/550_000_000);
                }
                state.add(new Movie(i));
            }
        });
        System.out.println("Done adding");

    }
    private Hollow() {}
}
