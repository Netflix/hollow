package com.netflix.hollow.perf.producer;

import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowFilesystemPublisher;
import com.netflix.hollow.perf.producer.model.Book;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProducerPerfTest {

    // Defaults
    private static String label = "test";
    private static int numBooks = 50000;
    private static int countriesPerBook = 10;
    private static int chaptersPerBook = 3;
    private static int chapterContentSize = 1024;
    private static int scenesPerChapter = 2;
    private static int charactersPerScene = 3;
    private static int numArtists = 5000;
    private static int addsPerCycle = 500;
    private static int removesPerCycle = 200;
    private static int modificationsPerCycle = 2000;
    private static int numCycles = 20;
    private static int numRuns = 2;
    private static int threads = 1;
    private static String blobPath = "/tmp/hollow-perf-data";
    private static boolean partitioned = false;

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        printConfig();

        PerfMetrics metrics = new PerfMetrics();
        metrics.label = label;
        metrics.timestamp = Instant.now().toString();
        metrics.config.numBooks = numBooks;
        metrics.config.countriesPerBook = countriesPerBook;
        metrics.config.chaptersPerBook = chaptersPerBook;
        metrics.config.chapterContentSize = chapterContentSize;
        metrics.config.numCycles = numCycles;
        metrics.config.numRuns = numRuns;
        metrics.config.threads = threads;
        metrics.config.partitionedOrdinalMap = partitioned;

        for (int run = 0; run < numRuns; run++) {
            boolean isWarmup = (run == 0 && numRuns > 1);
            System.out.println("\n=== Run " + run + (isWarmup ? " (warmup)" : " (measured)") + " ===");

            cleanBlobPath();

            DeterministicDataPopulator populator = new DeterministicDataPopulator(
                    numBooks, countriesPerBook, chaptersPerBook, chapterContentSize,
                    scenesPerChapter, charactersPerScene, numArtists,
                    addsPerCycle, removesPerCycle, modificationsPerCycle);

            System.out.println("Generating initial catalog...");
            populator.generateInitialCatalog();
            System.out.println("Initial catalog: " + populator.getBooks().size() + " book records");

            PerfMetricsListener listener = new PerfMetricsListener();
            HollowProducer producer = buildProducer(listener);

            ExecutorService executor = threads > 1 ? Executors.newFixedThreadPool(threads) : null;

            PerfMetrics.RunMetrics runMetrics = new PerfMetrics.RunMetrics();
            runMetrics.runIndex = run;

            // Cycle 0: initial snapshot
            System.out.println("Cycle 0 (snapshot)...");
            runCycle(producer, populator.getBooks(), executor);
            PerfMetrics.CycleMetrics cycleMetrics = listener.getCurrentCycle();
            cycleMetrics.cycleNum = 0;
            cycleMetrics.snapshotSizeBytes = getSnapshotSize();
            printCycleSummary(cycleMetrics);
            runMetrics.cycles.add(cycleMetrics);

            // Cycles 1..numCycles-1
            for (int c = 1; c < numCycles; c++) {
                System.out.println("Cycle " + c + "...");
                populator.applyDeltaModifications(c);
                runCycle(producer, populator.getBooks(), executor);
                PerfMetrics.CycleMetrics cm = listener.getCurrentCycle();
                cm.cycleNum = c;
                cm.snapshotSizeBytes = getSnapshotSize();
                printCycleSummary(cm);
                runMetrics.cycles.add(cm);
            }

            // Restore test
            System.out.println("\n--- Restore test ---");
            long lastVersion = runMetrics.cycles.get(runMetrics.cycles.size() - 1).version;
            PerfMetricsListener restoreListener = new PerfMetricsListener();
            HollowProducer restoreProducer = buildProducer(restoreListener);

            HollowFilesystemBlobRetriever retriever = new HollowFilesystemBlobRetriever(Paths.get(blobPath));
            restoreProducer.initializeDataModel(Book.class);
            restoreProducer.restore(lastVersion, retriever);

            PerfMetrics.RestoreMetrics restoreM = restoreListener.getRestoreMetrics();
            if (restoreM == null) {
                restoreM = new PerfMetrics.RestoreMetrics();
            }
            System.out.println("Restore duration: " + restoreM.restoreDurationMs + "ms");

            // Post-restore cycle
            System.out.println("Post-restore cycle...");
            populator.applyDeltaModifications(numCycles);
            runCycle(restoreProducer, populator.getBooks(), executor);
            restoreM.postRestoreCycleDurationMs = restoreListener.getCurrentCycle().cycleDurationMs;
            System.out.println("Post-restore cycle duration: " + restoreM.postRestoreCycleDurationMs + "ms");
            runMetrics.restore = restoreM;

            if (executor != null) {
                executor.shutdown();
            }

            if (isWarmup) {
                System.out.println("\nWarmup complete, discarding metrics");
            } else {
                metrics.runs.add(runMetrics);
            }

            cleanBlobPath();
        }

        metrics.computeSummary();
        String outputFile = "perf-results-" + label + ".json";
        writeStringToFile(outputFile, metrics.toJson());
        System.out.println("\nResults written to: " + outputFile);

        if (metrics.summary != null) {
            System.out.println("\n=== Summary ===");
            System.out.println("Avg cycle:    " + metrics.summary.avgCycleDurationMs + "ms");
            System.out.println("P50 cycle:    " + metrics.summary.p50CycleDurationMs + "ms");
            System.out.println("P95 cycle:    " + metrics.summary.p95CycleDurationMs + "ms");
            System.out.println("Avg populate: " + metrics.summary.avgPopulateDurationMs + "ms");
            System.out.println("Avg publish:  " + metrics.summary.avgPublishDurationMs + "ms");
        }
    }

    private static void runCycle(HollowProducer producer, List<Book> books, ExecutorService executor) throws Exception {
        producer.runCycle(state -> {
            if (executor != null) {
                parallelPopulate(state, books, executor);
            } else {
                for (Book book : books) {
                    state.add(book);
                }
            }
        });
    }

    private static void parallelPopulate(HollowProducer.WriteState state, List<Book> books, ExecutorService executor) {
        List<List<Book>> partitions = partitionList(books, threads);
        List<Future<?>> futures = new ArrayList<>(partitions.size());
        for (List<Book> partition : partitions) {
            futures.add(executor.submit(() -> {
                for (Book book : partition) {
                    state.add(book);
                }
            }));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException("Parallel populate failed", e);
            }
        }
    }

    private static <T> List<List<T>> partitionList(List<T> list, int n) {
        List<List<T>> partitions = new ArrayList<>(n);
        int size = list.size();
        int chunkSize = (size + n - 1) / n;
        for (int i = 0; i < n; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, size);
            if (start < end) {
                partitions.add(list.subList(start, end));
            }
        }
        return partitions;
    }

    private static HollowProducer buildProducer(PerfMetricsListener listener) {
        HollowProducer.Builder<?> builder = HollowProducer.withPublisher(new HollowFilesystemPublisher(Paths.get(blobPath)))
                .withListener(listener)
                .noIntegrityCheck();

        if (partitioned) {
            enablePartitionedOrdinalMap(builder);
        }

        return builder.build();
    }

    private static void enablePartitionedOrdinalMap(HollowProducer.Builder<?> builder) {
        try {
            Method m = builder.getClass().getMethod("withPartitionedOrdinalMap", boolean.class);
            m.invoke(builder, true);
            System.out.println("Partitioned ordinal map: ENABLED");
        } catch (NoSuchMethodException e) {
            System.out.println("Partitioned ordinal map: NOT AVAILABLE on this branch");
        } catch (Exception e) {
            System.out.println("Failed to enable partitioned ordinal map: " + e.getMessage());
        }
    }

    private static long getSnapshotSize() {
        File dir = new File(blobPath);
        File[] files = dir.listFiles();
        if (files == null) return 0;
        long maxSize = 0;
        for (File f : files) {
            if (f.getName().startsWith("snapshot-")) {
                maxSize = Math.max(maxSize, f.length());
            }
        }
        return maxSize;
    }

    private static void cleanBlobPath() throws IOException {
        Path path = Paths.get(blobPath);
        if (Files.exists(path)) {
            File[] files = path.toFile().listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            Files.deleteIfExists(path);
        }
        Files.createDirectories(path);
    }

    private static void writeStringToFile(String filePath, String content) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
    }

    private static void printCycleSummary(PerfMetrics.CycleMetrics cm) {
        System.out.printf("  cycle=%d version=%d total=%dms populate=%dms publish=%dms snapshot=%s heap=%s%n",
                cm.cycleNum, cm.version, cm.cycleDurationMs, cm.populateDurationMs, cm.publishDurationMs,
                humanSize(cm.snapshotSizeBytes), humanSize(cm.heapUsedBytes));
    }

    private static String humanSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.1fKB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1fMB", bytes / (1024.0 * 1024));
        return String.format("%.2fGB", bytes / (1024.0 * 1024 * 1024));
    }

    private static void printConfig() {
        System.out.println("=== Hollow Producer Performance Test ===");
        System.out.println("Label:              " + label);
        System.out.println("Books:              " + numBooks);
        System.out.println("Countries/book:     " + countriesPerBook);
        System.out.println("Chapters/book:      " + chaptersPerBook);
        System.out.println("Chapter content:    " + chapterContentSize + " bytes");
        System.out.println("Scenes/chapter:     " + scenesPerChapter);
        System.out.println("Characters/scene:   " + charactersPerScene);
        System.out.println("Artists:            " + numArtists);
        System.out.println("Cycles:             " + numCycles);
        System.out.println("Runs:               " + numRuns + " (first is warmup if >1)");
        System.out.println("Threads:            " + threads);
        System.out.println("Blob path:          " + blobPath);
        System.out.println("Partitioned:        " + partitioned);
        System.out.println("Total book records: ~" + (numBooks * countriesPerBook));
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--label": label = args[++i]; break;
                case "--num-books": numBooks = Integer.parseInt(args[++i]); break;
                case "--countries-per-book": countriesPerBook = Integer.parseInt(args[++i]); break;
                case "--chapters-per-book": chaptersPerBook = Integer.parseInt(args[++i]); break;
                case "--chapter-content-size": chapterContentSize = Integer.parseInt(args[++i]); break;
                case "--scenes-per-chapter": scenesPerChapter = Integer.parseInt(args[++i]); break;
                case "--characters-per-scene": charactersPerScene = Integer.parseInt(args[++i]); break;
                case "--num-artists": numArtists = Integer.parseInt(args[++i]); break;
                case "--adds-per-cycle": addsPerCycle = Integer.parseInt(args[++i]); break;
                case "--removes-per-cycle": removesPerCycle = Integer.parseInt(args[++i]); break;
                case "--modifications-per-cycle": modificationsPerCycle = Integer.parseInt(args[++i]); break;
                case "--num-cycles": numCycles = Integer.parseInt(args[++i]); break;
                case "--num-runs": numRuns = Integer.parseInt(args[++i]); break;
                case "--threads": threads = Integer.parseInt(args[++i]); break;
                case "--blob-path": blobPath = args[++i]; break;
                case "--partitioned": partitioned = Boolean.parseBoolean(args[++i]); break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(1);
            }
        }
    }
}
