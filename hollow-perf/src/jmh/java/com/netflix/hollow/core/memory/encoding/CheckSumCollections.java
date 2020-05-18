package com.netflix.hollow.core.memory.encoding;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
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
public class CheckSumCollections {

    static class Model {
        List<Integer> l;
        Set<Integer> s;
        Map<Integer, Integer> m;

        Model(List<Integer> l) {
            this.l = l;
        }

        Model(Set<Integer> s) {
            this.s = s;
        }

        Model(Map<Integer, Integer> m) {
            this.m = m;
        }
    }

    public enum Type {
        List, Set, Map
    }

    HollowReadStateEngine readState;

    HollowTypeReadState typeReadState;
    HollowSchema schema;

    @Param("List")
    private Type type = Type.Map;

    @Param("100")
    private int n = 1000;

    @Param("100")
    private int size = 1000;

    @Param("8")
    private int shards = 8;

    @Param("false")
    private boolean remove = false;

    @Setup
    public void setUp() throws IOException {
        HollowWriteStateEngine w = new HollowWriteStateEngine();

        HollowTypeWriteState typeWriteState;
        IntFunction<Model> f;
        String schemaName;
        switch (type) {
            case List:
                schemaName = "ListOfInteger";
                typeWriteState = new HollowListTypeWriteState(
                        new HollowListSchema(schemaName, "Integer"),
                        shards);
                f = i -> new Model(IntStream.range(i, i + size).boxed().collect(toList()));
                break;
            case Set:
                schemaName = "SetOfInteger";
                typeWriteState = new HollowSetTypeWriteState(
                        new HollowSetSchema(schemaName, "Integer"),
                        shards);
                f = i -> new Model(IntStream.range(i, i + size).boxed().collect(toSet()));
                break;
            case Map:
                schemaName = "MapOfIntegerToInteger";
                typeWriteState = new HollowMapTypeWriteState(
                        new HollowMapSchema(schemaName, "Integer", "Integer"),
                        shards);
                f = i -> new Model(IntStream.range(i, i + size).boxed().collect(toMap(e -> e, e -> e)));
                break;
            default:
                throw new Error();
        }
        w.addTypeState(typeWriteState);
        HollowObjectMapper m = new HollowObjectMapper(w);

        for (int i = 0; i < n; i++) {
            m.add(f.apply(i));
        }

        readState = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(w, readState);

        if (remove) {
            for (int i = 0; i < n; i++) {
                if (i % 3 == 0) {
                    m.add(f.apply(i));
                }
            }

            readState = new HollowReadStateEngine();
            StateEngineRoundTripper.roundTripSnapshot(w, readState);
        }

        typeReadState = readState.getTypeState(schemaName);
        schema = typeReadState.getSchema();
    }

    // Reads

    @Benchmark
    public int checkSum() {
        return typeReadState.getChecksum(schema).intValue();
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("CHECK", "true");

        CheckSumCollections x = new CheckSumCollections();
        x.setUp();
        x.checkSum();
    }
}

/*

Small number of small collections and no shards.
----

Old checksum loop
--
Benchmark                     (n)  (remove)  (shards)  (size)  (type)  Mode  Cnt      Score     Error  Units
CheckSumCollections.checkSum  100     false         1       5    List  avgt    5   8620.064 ±  76.049  ns/op
CheckSumCollections.checkSum  100     false         1       5     Set  avgt    5   8892.214 ± 560.921  ns/op
CheckSumCollections.checkSum  100     false         1       5     Map  avgt    5  11697.191 ± 973.119  ns/op
CheckSumCollections.checkSum  100      true         1       5    List  avgt    5   2989.913 ± 191.445  ns/op
CheckSumCollections.checkSum  100      true         1       5     Set  avgt    5   2997.619 ± 221.645  ns/op
CheckSumCollections.checkSum  100      true         1       5     Map  avgt    5   4053.238 ± 460.839  ns/op

New checksum loop
--
Benchmark                     (n)  (remove)  (shards)  (size)  (type)  Mode  Cnt      Score      Error  Units
CheckSumCollections.checkSum  100     false         1       5    List  avgt    5   8629.582 ±  870.650  ns/op
CheckSumCollections.checkSum  100     false         1       5     Set  avgt    5   8775.555 ±  407.123  ns/op
CheckSumCollections.checkSum  100     false         1       5     Map  avgt    5  11713.213 ± 1160.021  ns/op
CheckSumCollections.checkSum  100      true         1       5    List  avgt    5   2950.090 ±   94.284  ns/op
CheckSumCollections.checkSum  100      true         1       5     Set  avgt    5   2949.931 ±   28.125  ns/op
CheckSumCollections.checkSum  100      true         1       5     Map  avgt    5   4024.719 ±   64.082  ns/op

Results show no regressions with new checksum loop.


Large number of small collections with increasing shards
----

Old checksum loop
--
Benchmark                        (n)  (remove)  (shards)  (size)  (type)  Mode  Cnt         Score         Error  Units
CheckSumCollections.checkSum  100000     false         1       5    List  avgt    5   8596610.673 ±  690674.898  ns/op
CheckSumCollections.checkSum  100000     false         2       5    List  avgt    5   8921474.548 ±  617572.581  ns/op
CheckSumCollections.checkSum  100000     false         4       5    List  avgt    5   9777300.517 ±  824076.745  ns/op
CheckSumCollections.checkSum  100000     false         8       5    List  avgt    5  11111542.572 ± 1845196.989  ns/op
CheckSumCollections.checkSum  100000     false        16       5    List  avgt    5  14216973.678 ±  930008.717  ns/op
CheckSumCollections.checkSum  100000     false        32       5    List  avgt    5  20295407.292 ±  143616.170  ns/op
CheckSumCollections.checkSum  100000     false        64       5    List  avgt    5  33702448.882 ± 2725699.235  ns/op
CheckSumCollections.checkSum  100000     false         1       5     Set  avgt    5   9814348.577 ±  787704.885  ns/op
CheckSumCollections.checkSum  100000     false         2       5     Set  avgt    5  11170034.021 ±  675925.787  ns/op
CheckSumCollections.checkSum  100000     false         4       5     Set  avgt    5  12178179.723 ±  160719.626  ns/op
CheckSumCollections.checkSum  100000     false         8       5     Set  avgt    5  14104921.070 ± 1424761.987  ns/op
CheckSumCollections.checkSum  100000     false        16       5     Set  avgt    5  17494716.566 ±  214137.176  ns/op
CheckSumCollections.checkSum  100000     false        32       5     Set  avgt    5  25036660.934 ± 1996250.093  ns/op
CheckSumCollections.checkSum  100000     false        64       5     Set  avgt    5  37965918.676 ± 2671357.342  ns/op
CheckSumCollections.checkSum  100000     false         1       5     Map  avgt    5  12865598.249 ±  937229.106  ns/op
CheckSumCollections.checkSum  100000     false         2       5     Map  avgt    5  14121394.461 ± 1054106.360  ns/op
CheckSumCollections.checkSum  100000     false         4       5     Map  avgt    5  15375576.497 ± 1323319.531  ns/op
CheckSumCollections.checkSum  100000     false         8       5     Map  avgt    5  17339022.521 ± 1372546.941  ns/op
CheckSumCollections.checkSum  100000     false        16       5     Map  avgt    5  20806426.523 ± 1809170.036  ns/op
CheckSumCollections.checkSum  100000     false        32       5     Map  avgt    5  28236386.475 ± 1311581.690  ns/op
CheckSumCollections.checkSum  100000     false        64       5     Map  avgt    5  40241047.257 ± 2318284.057  ns/op
CheckSumCollections.checkSum  100000      true         1       5    List  avgt    5   2839641.637 ±   12376.643  ns/op
CheckSumCollections.checkSum  100000      true         2       5    List  avgt    5   2990283.962 ±  162829.807  ns/op
CheckSumCollections.checkSum  100000      true         4       5    List  avgt    5   3284778.179 ±  217469.913  ns/op
CheckSumCollections.checkSum  100000      true         8       5    List  avgt    5   3740442.175 ±  218747.530  ns/op
CheckSumCollections.checkSum  100000      true        16       5    List  avgt    5   4724311.888 ±  317028.345  ns/op
CheckSumCollections.checkSum  100000      true        32       5    List  avgt    5   6812155.755 ±   56129.624  ns/op
CheckSumCollections.checkSum  100000      true        64       5    List  avgt    5  11197537.392 ±  726553.201  ns/op
CheckSumCollections.checkSum  100000      true         1       5     Set  avgt    5   3685641.952 ±  255347.469  ns/op
CheckSumCollections.checkSum  100000      true         2       5     Set  avgt    5   3930741.424 ±  407687.414  ns/op
CheckSumCollections.checkSum  100000      true         4       5     Set  avgt    5   4186047.862 ±  442308.278  ns/op
CheckSumCollections.checkSum  100000      true         8       5     Set  avgt    5   4833507.041 ±  441235.419  ns/op
CheckSumCollections.checkSum  100000      true        16       5     Set  avgt    5   6054760.912 ±  501335.774  ns/op
CheckSumCollections.checkSum  100000      true        32       5     Set  avgt    5   8316517.003 ± 1078055.050  ns/op
CheckSumCollections.checkSum  100000      true        64       5     Set  avgt    5  12462405.661 ±  781315.293  ns/op
CheckSumCollections.checkSum  100000      true         1       5     Map  avgt    5   4765964.359 ±  400387.223  ns/op
CheckSumCollections.checkSum  100000      true         2       5     Map  avgt    5   4908237.594 ±  444764.593  ns/op
CheckSumCollections.checkSum  100000      true         4       5     Map  avgt    5   5316602.781 ±  660639.621  ns/op
CheckSumCollections.checkSum  100000      true         8       5     Map  avgt    5   5798124.648 ±  492634.889  ns/op
CheckSumCollections.checkSum  100000      true        16       5     Map  avgt    5   7135686.359 ±  562515.151  ns/op
CheckSumCollections.checkSum  100000      true        32       5     Map  avgt    5   9403437.346 ±  640716.225  ns/op
CheckSumCollections.checkSum  100000      true        64       5     Map  avgt    5  13511094.454 ± 1048413.356  ns/op

New checksum loop
--
Benchmark                        (n)  (remove)  (shards)  (size)  (type)  Mode  Cnt         Score         Error  Units
CheckSumCollections.checkSum  100000     false         1       5    List  avgt    5   8620584.454 ±  702278.822  ns/op
CheckSumCollections.checkSum  100000     false         2       5    List  avgt    5   8575850.611 ±  555832.071  ns/op
CheckSumCollections.checkSum  100000     false         4       5    List  avgt    5   8584383.175 ±  562913.671  ns/op
CheckSumCollections.checkSum  100000     false         8       5    List  avgt    5   8532158.012 ±  207276.206  ns/op
CheckSumCollections.checkSum  100000     false        16       5    List  avgt    5   8447779.118 ±  499044.657  ns/op
CheckSumCollections.checkSum  100000     false        32       5    List  avgt    5   8451732.010 ±  644022.611  ns/op
CheckSumCollections.checkSum  100000     false        64       5    List  avgt    5   8499921.507 ±  753095.911  ns/op
CheckSumCollections.checkSum  100000     false         1       5     Set  avgt    5   9903639.521 ±  495051.194  ns/op
CheckSumCollections.checkSum  100000     false         2       5     Set  avgt    5  10588014.844 ±  770598.836  ns/op
CheckSumCollections.checkSum  100000     false         4       5     Set  avgt    5  11390983.126 ±  913094.206  ns/op
CheckSumCollections.checkSum  100000     false         8       5     Set  avgt    5  11607058.027 ±  870592.896  ns/op
CheckSumCollections.checkSum  100000     false        16       5     Set  avgt    5  11250836.388 ±  660928.743  ns/op
CheckSumCollections.checkSum  100000     false        32       5     Set  avgt    5  11338874.644 ±  635317.786  ns/op
CheckSumCollections.checkSum  100000     false        64       5     Set  avgt    5  11139918.800 ±  888915.970  ns/op
CheckSumCollections.checkSum  100000     false         1       5     Map  avgt    5  12734036.092 ±  838503.473  ns/op
CheckSumCollections.checkSum  100000     false         2       5     Map  avgt    5  13674956.077 ± 1149760.294  ns/op
CheckSumCollections.checkSum  100000     false         4       5     Map  avgt    5  14134753.803 ± 1068061.447  ns/op
CheckSumCollections.checkSum  100000     false         8       5     Map  avgt    5  14276311.270 ±  885502.514  ns/op
CheckSumCollections.checkSum  100000     false        16       5     Map  avgt    5  14245044.137 ±  918599.933  ns/op
CheckSumCollections.checkSum  100000     false        32       5     Map  avgt    5  14248491.342 ±  281088.157  ns/op
CheckSumCollections.checkSum  100000     false        64       5     Map  avgt    5  14416292.644 ±  740792.676  ns/op
CheckSumCollections.checkSum  100000      true         1       5    List  avgt    5   2897102.147 ±  413392.257  ns/op
CheckSumCollections.checkSum  100000      true         2       5    List  avgt    5   3026996.759 ±  304404.892  ns/op
CheckSumCollections.checkSum  100000      true         4       5    List  avgt    5   3150420.221 ±  161872.430  ns/op
CheckSumCollections.checkSum  100000      true         8       5    List  avgt    5   3046548.631 ±   22866.490  ns/op
CheckSumCollections.checkSum  100000      true        16       5    List  avgt    5   3091243.402 ±  183414.075  ns/op
CheckSumCollections.checkSum  100000      true        32       5    List  avgt    5   3174963.994 ±  158946.528  ns/op
CheckSumCollections.checkSum  100000      true        64       5    List  avgt    5   3218827.226 ±  396990.207  ns/op
CheckSumCollections.checkSum  100000      true         1       5     Set  avgt    5   3663434.272 ±  210262.598  ns/op
CheckSumCollections.checkSum  100000      true         2       5     Set  avgt    5   3899553.638 ±  111356.848  ns/op
CheckSumCollections.checkSum  100000      true         4       5     Set  avgt    5   4169134.313 ±  411031.394  ns/op
CheckSumCollections.checkSum  100000      true         8       5     Set  avgt    5   4035417.228 ±   31272.379  ns/op
CheckSumCollections.checkSum  100000      true        16       5     Set  avgt    5   4081185.262 ±  358245.310  ns/op
CheckSumCollections.checkSum  100000      true        32       5     Set  avgt    5   4005187.952 ±  262924.102  ns/op
CheckSumCollections.checkSum  100000      true        64       5     Set  avgt    5   4135250.793 ±  302641.176  ns/op
CheckSumCollections.checkSum  100000      true         1       5     Map  avgt    5   4752774.882 ±  303454.606  ns/op
CheckSumCollections.checkSum  100000      true         2       5     Map  avgt    5   5098274.846 ±  453128.125  ns/op
CheckSumCollections.checkSum  100000      true         4       5     Map  avgt    5   5218318.468 ±  483634.597  ns/op
CheckSumCollections.checkSum  100000      true         8       5     Map  avgt    5   5206996.794 ±  464277.681  ns/op
CheckSumCollections.checkSum  100000      true        16       5     Map  avgt    5   5098079.831 ±  372875.532  ns/op
CheckSumCollections.checkSum  100000      true        32       5     Map  avgt    5   5553091.761 ± 1088287.273  ns/op
CheckSumCollections.checkSum  100000      true        64       5     Map  avgt    5   5192518.094 ±  161140.260  ns/op


Results show that for the old checksum loop the time increases as the shards increase where as for the new
checksum loop the time is approximately constant (a slight increase with shards). For a list with a shard size
of 64 the new checksum loop is (33702448.882 / 8499921.507 = 3.97, 11197537.392 / 3218827.226 = 3.48) is 3x to 4x
faster.

 */
