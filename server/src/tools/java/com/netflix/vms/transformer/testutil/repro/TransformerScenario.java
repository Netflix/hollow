package com.netflix.vms.transformer.testutil.repro;

import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.INPUT_VERSION_KEY_PREFIX;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.UpstreamDatasetConfig;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.UpstreamDatasetConfig.getNamespaces;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.UpstreamDatasetConfig.getNamespacesforEnv;
import static com.netflix.vms.transformer.input.UpstreamDatasetHolder.UpstreamDatasetConfig.lookupDatasetForNamespace;

import com.netflix.cinder.consumer.CinderConsumerBuilder;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.SimpleTransformer;
import com.netflix.vms.transformer.SimpleTransformerContext;
import com.netflix.vms.transformer.VMSTransformerWriteStateEngine;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.consumer.VMSInputDataConsumer;
import com.netflix.vms.transformer.http.HttpHelper;
import com.netflix.vms.transformer.input.CycleInputs;
import com.netflix.vms.transformer.input.UpstreamDatasetHolder;
import com.netflix.vms.transformer.input.datasets.slicers.SlicerFactory;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

public class TransformerScenario {

    private final String localBlobStore;
    private static final boolean IS_PROD = false;

    private final Supplier<CinderConsumerBuilder> cinderConsumerBuilder;
    private final String transformerVip;
    private final long outputDataVersion;
    private final int[] topNodesToProcess;

    private Map<UpstreamDatasetHolder.Dataset, Long> inputVersions;
    private long processTimestamp;

    private static final String PROXY_URL = IS_PROD ? VMSInputDataConsumer.PROD_PROXY_URL : VMSInputDataConsumer.TEST_PROXY_URL;

    public TransformerScenario(Supplier<CinderConsumerBuilder> cinderConsumerBuilder, String transformerVip, String localBlobStore, long outputDataVersion, int... topNodesToProcess) {
        this.cinderConsumerBuilder = cinderConsumerBuilder;
        this.transformerVip = transformerVip;
        this.localBlobStore = localBlobStore;
        this.outputDataVersion = outputDataVersion;
        this.topNodesToProcess = topNodesToProcess;
    }

    public VMSTransformerWriteStateEngine repro() throws Throwable {
        Map<UpstreamDatasetHolder.Dataset, File> scenarioInputFiles = scenarioInputDataFiles();

        Map<UpstreamDatasetHolder.Dataset, InputState> inputs = new HashMap<>();
        for (Map.Entry<UpstreamDatasetHolder.Dataset, File> entry : scenarioInputFiles.entrySet()) {
            UpstreamDatasetHolder.Dataset dataset = entry.getKey();
            File file = entry.getValue();

            HollowReadStateEngine inputStateEngineSlice;
            if(file.exists()) {
                inputStateEngineSlice = readStateEngineSlice(file);
            } else {
                inputStateEngineSlice = createStateEngineSlice(file, dataset);
            }
            inputs.put(dataset, new InputState(inputStateEngineSlice, 1l));
        }
        CycleInputs cycleInputs = new CycleInputs(inputs, outputDataVersion);

        VMSTransformerWriteStateEngine outputStateEngine = new VMSTransformerWriteStateEngine();
        SimpleTransformerContext ctx = new SimpleTransformerContext();
        ctx.setNowMillis(processTimestamp);



        new SimpleTransformer(cycleInputs, outputStateEngine, ctx).transform();

        return outputStateEngine;
    }

    private HollowReadStateEngine createStateEngineSlice(File scenarioInputFile, UpstreamDatasetHolder.Dataset dataset) throws Exception {
        determineInputParameters();

        HollowConsumer inputConsumer = VMSInputDataConsumer.getNewProxyConsumer(cinderConsumerBuilder, getNamespacesforEnv(IS_PROD).get(dataset),
                localBlobStore, IS_PROD, dataset.getAPI());

        inputConsumer.triggerRefreshTo(inputVersions.get(dataset));

        if (dataset.getSlicer() == null) {
            throw new UnsupportedOperationException("Input data slicer missing for dataset= " + dataset);
        }

        InputDataSlicer inputDataSlicer = new SlicerFactory().getInputDataSlicer(dataset, 0, topNodesToProcess);
        HollowWriteStateEngine slicedStateEngine = inputDataSlicer.sliceInputBlob(inputConsumer.getStateEngine());

        slicedStateEngine.addHeaderTag("publishCycleDataTS", String.valueOf(processTimestamp));

        writeStateEngineSlice(slicedStateEngine, scenarioInputFile);

        return readStateEngineSlice(scenarioInputFile);
    }

    private void determineInputParameters() throws IOException {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(transformerVip);

        Properties dataProps = new Properties();
        InputStream is = HttpHelper.getInputStream(PROXY_URL + "/filestore-attribute?keybase=" + keybaseBuilder.getReverseDeltaKeybase() + "&version=" + outputDataVersion);
        dataProps.load(is);

        this.inputVersions = getInputVersionsFromProps(dataProps);
        this.processTimestamp = Long.parseLong(dataProps.getProperty("publishCycleDataTS"));
    }

    private Map<UpstreamDatasetHolder.Dataset, Long> getInputVersionsFromProps(Properties dataProps) {
        Map<UpstreamDatasetHolder.Dataset, Long> inputVersions = new HashMap<>();
        dataProps.forEach((k,v) -> {
            if (k.toString().startsWith(INPUT_VERSION_KEY_PREFIX) && k.toString().length() != INPUT_VERSION_KEY_PREFIX.length()) {
                String namespace = k.toString().substring(INPUT_VERSION_KEY_PREFIX.length());
                inputVersions.put(lookupDatasetForNamespace(namespace, IS_PROD), Long.valueOf(v.toString()));
            }
        });
        if (inputVersions.size() != getNamespaces().size()) {
            String msg = "There was a mismatch in inputs expected of the env and inputs present in the properties"
                    + "(expected=" + getNamespaces().size() + ", actual=" + inputVersions.size() + ").";
            System.out.println("FATAL: " + msg);
            throw new IllegalArgumentException(msg);
        }
        return inputVersions;
    }

    private Map<UpstreamDatasetHolder.Dataset, File> scenarioInputDataFiles() {
        Map<UpstreamDatasetHolder.Dataset, File> files = new HashMap<>();
        for (UpstreamDatasetHolder.Dataset dataset: UpstreamDatasetConfig.getNamespacesforEnv(IS_PROD).keySet()) {
            String namespace = UpstreamDatasetConfig.getNamespacesforEnv(IS_PROD).get(dataset);
            files.put(dataset, new File(localBlobStore, "scenario-" + transformerVip + "-" + outputDataVersion
                    + "-" + namespace + "-" + Integer.toHexString(inputVideosHashCode())));
        }
        return files;
    }

    private int inputVideosHashCode() {
        int hashCode = 0;
        for(int i : topNodesToProcess)
            hashCode ^= HashCodes.hashInt(i);
        return hashCode;
    }

    private HollowReadStateEngine readStateEngineSlice(File sliceFile) throws IOException {
        HollowReadStateEngine stateEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(stateEngine);
        try(LZ4BlockInputStream is = new LZ4BlockInputStream(new FileInputStream(sliceFile))) {
            reader.readSnapshot(is);
        }

        this.processTimestamp = Long.parseLong(stateEngine.getHeaderTag("publishCycleDataTS"));

        return stateEngine;
    }

    private void writeStateEngineSlice(HollowWriteStateEngine slicedStateEngine, File sliceFile) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(slicedStateEngine);
        try(LZ4BlockOutputStream os = new LZ4BlockOutputStream(new FileOutputStream(sliceFile))) {
            writer.writeSnapshot(os);
        }
    }

}
