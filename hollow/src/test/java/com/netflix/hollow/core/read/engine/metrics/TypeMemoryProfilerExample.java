package com.netflix.hollow.core.read.engine.metrics;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

/**
 * Example demonstrating type memory profiling usage.
 * Run with: ./gradlew test --tests TypeMemoryProfilerExample
 */
public class TypeMemoryProfilerExample {

    public static void main(String[] args) throws Exception {
        // Create dataset with various types
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);

        System.out.println("Generating test data...");

        // Small objects
        for (int i = 0; i < 10000; i++) {
            mapper.add(new User("user" + i, i));
        }

        // Medium objects
        for (int i = 0; i < 1000; i++) {
            mapper.add(new Product("product" + i, "Description " + i, i * 100.0));
        }

        // Large objects
        for (int i = 0; i < 100; i++) {
            byte[] data = new byte[50000];
            mapper.add(new Document("doc" + i, data));
        }

        System.out.println("Loading into consumer with profiling...");

        // Load with profiling
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        TypeMemoryProfiler profiler = new TypeMemoryProfiler();

        profiler.startProfiling(readEngine);

        // Simulate loading
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        com.netflix.hollow.core.write.HollowBlobWriter writer =
            new com.netflix.hollow.core.write.HollowBlobWriter(writeEngine);
        writer.writeSnapshot(baos);

        com.netflix.hollow.core.read.engine.HollowBlobReader reader =
            new com.netflix.hollow.core.read.engine.HollowBlobReader(readEngine);
        reader.readSnapshot(com.netflix.hollow.core.read.HollowBlobInput.serial(baos.toByteArray()));

        TypeMemoryProfiler.ProfileResult result = profiler.endProfiling();

        // Print results
        result.printSummary();
    }

    static class User {
        String username;
        int age;
        User(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }

    static class Product {
        String name;
        String description;
        double price;
        Product(String name, String description, double price) {
            this.name = name;
            this.description = description;
            this.price = price;
        }
    }

    static class Document {
        String title;
        byte[] content;
        Document(String title, byte[] content) {
            this.title = title;
            this.content = content;
        }
    }
}
