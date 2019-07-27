package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.UpstreamDatasetConfig;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.common.slice.InputDataSlicer;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.input.api.gen.gatekeeper2.Gk2StatusAPI;
import com.netflix.vms.transformer.input.api.gen.topn.TopNAPI;
import com.netflix.vms.transformer.input.datasets.ConverterDataset;
import com.netflix.vms.transformer.input.datasets.Gatekeeper2Dataset;
import com.netflix.vms.transformer.input.datasets.TopNDataset;
import com.netflix.vms.transformer.input.datasets.slicers.ConverterDataSlicerImpl;
import com.netflix.vms.transformer.input.datasets.slicers.Gk2StatusDataSlicerImpl;
import com.netflix.vms.transformer.input.datasets.slicers.TopNDataSlicerImpl;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpstreamDatasetHolder {

    private final Map<UpstreamDatasetHolder.Dataset, UpstreamDataset> inputs;

    private UpstreamDatasetHolder() {
        this.inputs = new ConcurrentHashMap<>();
    }

    public enum Dataset {

        CONVERTER(ConverterDataset.class, VMSHollowInputAPI.class, ConverterDataSlicerImpl.class),
        GATEKEEPER2(Gatekeeper2Dataset.class, Gk2StatusAPI.class, Gk2StatusDataSlicerImpl.class),
        TOPN(TopNDataset.class, TopNAPI.class, TopNDataSlicerImpl.class);
        //TODO: enable me once we can turn on the new data set including follow vip functionality
        //        OSCAR(OscarDataset.class, OscarAPI.class, OscarMovieDataSlicerImpl.class);

        private Class<? extends UpstreamDataset> upstream;
        private Class<? extends HollowAPI> api;
        private Class<? extends InputDataSlicer> slicer;
        Dataset(Class<? extends UpstreamDataset> upstream, Class<? extends HollowAPI> api, Class<? extends InputDataSlicer> slicer) {
            this.upstream = upstream;
            this.api = api;
            this.slicer = slicer;
        }

        public Class<? extends UpstreamDataset> getUpstream() {
            return upstream;
        }

        public Class<? extends HollowAPI> getAPI() {
            return api;
        }

        public Class<? extends InputDataSlicer> getSlicer() {
            return slicer;
        }
    }

    /**
     * Returns a new {@code UpstreamDataHolder} containing inputs
     */
    public static UpstreamDatasetHolder getNewDatasetHolder(
            Map<DatasetIdentifier, InputState> inputs) throws Exception {

        SimultaneousExecutor executor = new SimultaneousExecutor(Runtime.getRuntime().availableProcessors(), UpstreamDatasetHolder.class.getName());

        UpstreamDatasetHolder UpstreamDatasetHolder = new UpstreamDatasetHolder();

        for (DatasetIdentifier datasetIdentifier : DatasetIdentifier.values()) {
            Dataset dataset = Dataset.valueOf(datasetIdentifier.name());
            Class datasetClasz = dataset.getUpstream();
            Constructor datasetConstructor = datasetClasz.getConstructor(new Class[]{InputState.class});
            executor.execute(() -> {
                try {
                    if (inputs.get(datasetIdentifier) != null) {
                        UpstreamDatasetHolder.setDataset(datasetIdentifier, (UpstreamDataset) datasetConstructor.newInstance(inputs.get(datasetIdentifier)));
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(String.format("Unable to instantiate upstream data holder for input "
                            + "dataset=%s with namespace=%s", datasetIdentifier,
                            UpstreamDatasetConfig.getNamespaces().get(datasetIdentifier)));
                }
            });
        }

        try {
            executor.awaitSuccessfulCompletion();
        } catch(Throwable th) {
            throw new RuntimeException("Failed to index updated input state into upstream dataset holder", th);
        }

        return UpstreamDatasetHolder;
    }

    public void setDataset(DatasetIdentifier key, UpstreamDataset dataset) {
        this.inputs.put(Dataset.valueOf(key.name()), dataset);
    }

    @SuppressWarnings("unchecked")
    public <T extends UpstreamDataset> T getDataset(DatasetIdentifier datasetIdentifier) {
        return (T)inputs.get(Dataset.valueOf(datasetIdentifier.name()));
    }

}
