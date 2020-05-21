package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.memory.ByteArrayOrdinalMap;
import com.netflix.hollow.core.memory.ByteDataArray;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class OrdinalMapResize {

    @Param("256")
    int n = 256;

    @Param("32")
    int contentSize = 32;

    ByteDataArray[] content;

    @Setup
    public void setUp() {
        SplittableRandom r = new SplittableRandom(0);

        content = new ByteDataArray[n];
        for (int i = 0; i < n; i++) {
            ByteDataArray buf = new ByteDataArray();
            for (int j = 0; j < contentSize; j++) {
                buf.write((byte) r.nextInt(0, 256));
            }
            content[i] = buf;
        }
    }

    @Benchmark
    public ByteArrayOrdinalMap defaultGet() {
        ByteArrayOrdinalMap map = new ByteArrayOrdinalMap();
        for (int i = 0; i < n; i++) {
            map.getOrAssignOrdinal(content[i]);
        }
        return map;
    }

    @Benchmark
    public ByteArrayOrdinalMap sizedGet() {
        ByteArrayOrdinalMap map = new ByteArrayOrdinalMap(n << 1);
        for (int i = 0; i < n; i++) {
            map.getOrAssignOrdinal(content[i]);
        }
        return map;
    }
}

/*

Benchmark                    (contentSize)     (n)  Mode  Cnt          Score          Error  Units
OrdinalMapResize.defaultGet              8    2048  avgt    5     524669.559 ±    94661.993  ns/op
OrdinalMapResize.defaultGet              8    8192  avgt    5    2864984.097 ±   973573.341  ns/op
OrdinalMapResize.defaultGet              8   32768  avgt    5   14644216.493 ±   461996.760  ns/op
OrdinalMapResize.defaultGet              8  131072  avgt    5   64906816.288 ±  5527376.480  ns/op
OrdinalMapResize.defaultGet             16    2048  avgt    5     579804.853 ±    35227.500  ns/op
OrdinalMapResize.defaultGet             16    8192  avgt    5    3226875.857 ±  1200816.411  ns/op
OrdinalMapResize.defaultGet             16   32768  avgt    5   16333522.740 ±  1645155.319  ns/op
OrdinalMapResize.defaultGet             16  131072  avgt    5   72300430.931 ±  8243987.662  ns/op
OrdinalMapResize.defaultGet             32    2048  avgt    5     708348.638 ±    48480.615  ns/op
OrdinalMapResize.defaultGet             32    8192  avgt    5    3845712.806 ±   659749.886  ns/op
OrdinalMapResize.defaultGet             32   32768  avgt    5   19132132.938 ±  2578367.604  ns/op
OrdinalMapResize.defaultGet             32  131072  avgt    5   81810115.046 ±  5377543.308  ns/op
OrdinalMapResize.defaultGet             64    2048  avgt    5     988812.959 ±   139916.394  ns/op
OrdinalMapResize.defaultGet             64    8192  avgt    5    5247170.001 ±   961661.753  ns/op
OrdinalMapResize.defaultGet             64   32768  avgt    5   24915997.247 ±  5133110.667  ns/op
OrdinalMapResize.defaultGet             64  131072  avgt    5  106995398.542 ± 14459051.669  ns/op
OrdinalMapResize.defaultGet            128    2048  avgt    5    1575062.220 ±   110460.192  ns/op
OrdinalMapResize.defaultGet            128    8192  avgt    5    7735092.080 ±   411007.955  ns/op
OrdinalMapResize.defaultGet            128   32768  avgt    5   35920602.082 ±  7833800.705  ns/op
OrdinalMapResize.defaultGet            128  131072  avgt    5  156354984.171 ± 13402008.695  ns/op
OrdinalMapResize.sizedGet                8    2048  avgt    5     196358.861 ±    15371.176  ns/op
OrdinalMapResize.sizedGet                8    8192  avgt    5    1250185.898 ±   337449.745  ns/op
OrdinalMapResize.sizedGet                8   32768  avgt    5    7569535.480 ±   768681.941  ns/op
OrdinalMapResize.sizedGet                8  131072  avgt    5   34166531.026 ±  2601826.357  ns/op
OrdinalMapResize.sizedGet               16    2048  avgt    5     233474.915 ±     4107.039  ns/op
OrdinalMapResize.sizedGet               16    8192  avgt    5    1618315.403 ±   503515.257  ns/op
OrdinalMapResize.sizedGet               16   32768  avgt    5    8423144.202 ±  1531274.146  ns/op
OrdinalMapResize.sizedGet               16  131072  avgt    5   37380396.196 ±  3756140.945  ns/op
OrdinalMapResize.sizedGet               32    2048  avgt    5     291975.497 ±    30660.463  ns/op
OrdinalMapResize.sizedGet               32    8192  avgt    5    1930925.813 ±   162060.695  ns/op
OrdinalMapResize.sizedGet               32   32768  avgt    5    9944128.175 ±  2468853.190  ns/op
OrdinalMapResize.sizedGet               32  131072  avgt    5   43925098.941 ±  3336023.251  ns/op
OrdinalMapResize.sizedGet               64    2048  avgt    5     423357.848 ±    60510.201  ns/op
OrdinalMapResize.sizedGet               64    8192  avgt    5    2553086.582 ±   298923.441  ns/op
OrdinalMapResize.sizedGet               64   32768  avgt    5   12388266.041 ±  4230163.664  ns/op
OrdinalMapResize.sizedGet               64  131072  avgt    5   55145939.840 ±  6016040.570  ns/op
OrdinalMapResize.sizedGet              128    2048  avgt    5     687408.861 ±    39354.473  ns/op
OrdinalMapResize.sizedGet              128    8192  avgt    5    4023545.068 ±   124931.913  ns/op
OrdinalMapResize.sizedGet              128   32768  avgt    5   17758789.247 ±  5952902.676  ns/op
OrdinalMapResize.sizedGet              128  131072  avgt    5   77575714.649 ± 10985599.044  ns/op

 */