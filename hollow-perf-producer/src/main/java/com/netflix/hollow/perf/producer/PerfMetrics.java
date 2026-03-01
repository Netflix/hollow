package com.netflix.hollow.perf.producer;

import java.util.*;

public class PerfMetrics {

    public static class CycleMetrics {
        public int cycleNum;
        public long version;
        public long cycleDurationMs;
        public long populateDurationMs;
        public long publishDurationMs;
        public Map<String, Long> blobStagingMs = new LinkedHashMap<>();
        public Map<String, Long> blobPublishMs = new LinkedHashMap<>();
        public long snapshotSizeBytes;
        public long heapUsedBytes;
    }

    public static class RestoreMetrics {
        public long restoreDurationMs;
        public long restoredVersion;
        public long postRestoreCycleDurationMs;
    }

    public static class RunMetrics {
        public int runIndex;
        public List<CycleMetrics> cycles = new ArrayList<>();
        public RestoreMetrics restore;
    }

    public static class Config {
        public int numBooks;
        public int countriesPerBook;
        public int chaptersPerBook;
        public int chapterContentSize;
        public int numCycles;
        public int numRuns;
        public int threads;
        public boolean partitionedOrdinalMap;
    }

    public static class Summary {
        public long avgCycleDurationMs;
        public long p50CycleDurationMs;
        public long p95CycleDurationMs;
        public long avgPopulateDurationMs;
        public long avgPublishDurationMs;
    }

    public String label;
    public String timestamp;
    public Config config = new Config();
    public List<RunMetrics> runs = new ArrayList<>();
    public Summary summary;

    public void computeSummary() {
        List<Long> allCycleDurations = new ArrayList<>();
        List<Long> allPopulateDurations = new ArrayList<>();
        List<Long> allPublishDurations = new ArrayList<>();

        for (RunMetrics run : runs) {
            for (CycleMetrics cycle : run.cycles) {
                allCycleDurations.add(cycle.cycleDurationMs);
                allPopulateDurations.add(cycle.populateDurationMs);
                allPublishDurations.add(cycle.publishDurationMs);
            }
        }

        summary = new Summary();
        if (!allCycleDurations.isEmpty()) {
            summary.avgCycleDurationMs = avg(allCycleDurations);
            summary.p50CycleDurationMs = percentile(allCycleDurations, 50);
            summary.p95CycleDurationMs = percentile(allCycleDurations, 95);
            summary.avgPopulateDurationMs = avg(allPopulateDurations);
            summary.avgPublishDurationMs = avg(allPublishDurations);
        }
    }

    private static long avg(List<Long> values) {
        return (long) values.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    private static long percentile(List<Long> values, int p) {
        List<Long> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int idx = (int) Math.ceil(p / 100.0 * sorted.size()) - 1;
        return sorted.get(Math.max(0, idx));
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"label\": ").append(jsonStr(label)).append(",\n");
        sb.append("  \"timestamp\": ").append(jsonStr(timestamp)).append(",\n");
        appendConfig(sb);
        appendRuns(sb);
        appendSummary(sb);
        sb.append("}\n");
        return sb.toString();
    }

    private void appendConfig(StringBuilder sb) {
        sb.append("  \"config\": {\n");
        sb.append("    \"numBooks\": ").append(config.numBooks).append(",\n");
        sb.append("    \"countriesPerBook\": ").append(config.countriesPerBook).append(",\n");
        sb.append("    \"chaptersPerBook\": ").append(config.chaptersPerBook).append(",\n");
        sb.append("    \"chapterContentSize\": ").append(config.chapterContentSize).append(",\n");
        sb.append("    \"numCycles\": ").append(config.numCycles).append(",\n");
        sb.append("    \"numRuns\": ").append(config.numRuns).append(",\n");
        sb.append("    \"threads\": ").append(config.threads).append(",\n");
        sb.append("    \"partitionedOrdinalMap\": ").append(config.partitionedOrdinalMap).append("\n");
        sb.append("  },\n");
    }

    private void appendRuns(StringBuilder sb) {
        sb.append("  \"runs\": [\n");
        for (int r = 0; r < runs.size(); r++) {
            RunMetrics run = runs.get(r);
            sb.append("    {\n");
            sb.append("      \"runIndex\": ").append(run.runIndex).append(",\n");
            sb.append("      \"cycles\": [\n");
            for (int c = 0; c < run.cycles.size(); c++) {
                CycleMetrics cycle = run.cycles.get(c);
                sb.append("        {\n");
                sb.append("          \"cycleNum\": ").append(cycle.cycleNum).append(",\n");
                sb.append("          \"version\": ").append(cycle.version).append(",\n");
                sb.append("          \"cycleDurationMs\": ").append(cycle.cycleDurationMs).append(",\n");
                sb.append("          \"populateDurationMs\": ").append(cycle.populateDurationMs).append(",\n");
                sb.append("          \"publishDurationMs\": ").append(cycle.publishDurationMs).append(",\n");
                appendMap(sb, "blobStagingMs", cycle.blobStagingMs, 10);
                appendMap(sb, "blobPublishMs", cycle.blobPublishMs, 10);
                sb.append("          \"snapshotSizeBytes\": ").append(cycle.snapshotSizeBytes).append(",\n");
                sb.append("          \"heapUsedBytes\": ").append(cycle.heapUsedBytes).append("\n");
                sb.append("        }");
                if (c < run.cycles.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      ]");
            if (run.restore != null) {
                sb.append(",\n");
                sb.append("      \"restore\": {\n");
                sb.append("        \"restoreDurationMs\": ").append(run.restore.restoreDurationMs).append(",\n");
                sb.append("        \"restoredVersion\": ").append(run.restore.restoredVersion).append(",\n");
                sb.append("        \"postRestoreCycleDurationMs\": ").append(run.restore.postRestoreCycleDurationMs).append("\n");
                sb.append("      }\n");
            } else {
                sb.append("\n");
            }
            sb.append("    }");
            if (r < runs.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ],\n");
    }

    private void appendSummary(StringBuilder sb) {
        sb.append("  \"summary\": {\n");
        if (summary != null) {
            sb.append("    \"avgCycleDurationMs\": ").append(summary.avgCycleDurationMs).append(",\n");
            sb.append("    \"p50CycleDurationMs\": ").append(summary.p50CycleDurationMs).append(",\n");
            sb.append("    \"p95CycleDurationMs\": ").append(summary.p95CycleDurationMs).append(",\n");
            sb.append("    \"avgPopulateDurationMs\": ").append(summary.avgPopulateDurationMs).append(",\n");
            sb.append("    \"avgPublishDurationMs\": ").append(summary.avgPublishDurationMs).append("\n");
        }
        sb.append("  }\n");
    }

    private static void appendMap(StringBuilder sb, String name, Map<String, Long> map, int indent) {
        StringBuilder padBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) padBuilder.append(' ');
        String pad = padBuilder.toString();

        sb.append(pad).append("\"").append(name).append("\": {");
        if (map.isEmpty()) {
            sb.append("},\n");
            return;
        }
        sb.append("\n");
        List<Map.Entry<String, Long>> entries = new ArrayList<>(map.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, Long> e = entries.get(i);
            sb.append(pad).append("  ").append(jsonStr(e.getKey())).append(": ").append(e.getValue());
            if (i < entries.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(pad).append("},\n");
    }

    private static String jsonStr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
