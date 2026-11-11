Run the `jmhJar` task to produce an uber-jar in the `build/libs` directory.

Then do 

- `java -jar hollow-perf/build/libs/hollow-perf-*-jmh.jar -h` to print the help information
- `java -jar hollow-perf/build/libs/hollow-perf-*-jmh.jar -l` to list the benchmarks

An alternative execution is to run the `jmh` task but that will run Gradle in addition to the benchmark which might 
introduce more variance in the results.

If the annotations declared on benchmark classes are modified it may be necessary to kill Gradle daemons and
rebuild.

`FixedLengthSnapshotReadBenchmark` measures segmented and contiguous fixed-length snapshot loading from in-memory input, with and without buffering. Output allocation and loader scratch allocation are included; file I/O and decompression are not.

```bash
./gradlew :hollow-perf:jmhJar
java -jar hollow-perf/build/libs/hollow-perf-*-jmh.jar '.*FixedLengthSnapshotReadBenchmark.*' -prof gc -rf json -rff snapshot-reads.json
```

For before/after comparisons, run the same benchmark source against both revisions using the same JVM, heap settings, and JMH options.
