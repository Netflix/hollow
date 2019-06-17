package com.netflix.vms.transformer.common.input;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.Dataset.CONVERTER;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetHolder.Dataset.GATEKEEPER2;

import com.netflix.config.NetflixConfiguration;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.vms.transformer.common.input.datasets.ConverterDataset;
import com.netflix.vms.transformer.common.input.datasets.Gatekeeper2Dataset;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UpstreamDatasetHolder {

    private final Map<Dataset, UpstreamDataset> inputs;

    private UpstreamDatasetHolder() {
        this.inputs = new ConcurrentHashMap<>();
    }

    public static UpstreamDatasetHolder getNewDatasetHolder(Map<Dataset, InputState> inputs) {

        SimultaneousExecutor executor = new SimultaneousExecutor(Runtime.getRuntime().availableProcessors(), UpstreamDatasetHolder.class.getName());

        UpstreamDatasetHolder upstreamDatasetHolder = new UpstreamDatasetHolder();
        executor.execute(() -> upstreamDatasetHolder.setDataset(CONVERTER, new ConverterDataset(inputs.get(CONVERTER))));
        executor.execute(() -> upstreamDatasetHolder.setDataset(GATEKEEPER2, new Gatekeeper2Dataset(inputs.get(GATEKEEPER2))));

        try {
            executor.awaitSuccessfulCompletion();
        } catch(Throwable th) {
            throw new RuntimeException("Failed to index updated input state into upstream dataset holder", th);
        }

        return upstreamDatasetHolder;
    }

    /**
     * Input datasets are enumerated here. NOTE: some metadata attributes are computed based on calling toString() on these
     * enumerators so avoid renaming these enumerators as changes won't be backwards compatible.
     */
    public enum Dataset {
        CONVERTER,
        GATEKEEPER2
    }

    public static class UpstreamDatasetConfig {
        private static final Map<UpstreamDatasetHolder.Dataset, String> PROD_INPUT_NAMESPACES = new EnumMap<>(UpstreamDatasetHolder.Dataset.class);
        private static final Map<UpstreamDatasetHolder.Dataset, String> TEST_INPUT_NAMESPACES = new EnumMap<>(UpstreamDatasetHolder.Dataset.class);
        static {
            PROD_INPUT_NAMESPACES.put(CONVERTER, "vmsconverter-muon");
            PROD_INPUT_NAMESPACES.put(GATEKEEPER2, "gatekeeper2_status_prod");

            TEST_INPUT_NAMESPACES.put(CONVERTER, "vmsconverter-muon");
            TEST_INPUT_NAMESPACES.put(GATEKEEPER2, "gatekeeper2_status_test");
        }

        public static final Map<UpstreamDatasetHolder.Dataset, Class<? extends HollowAPI>> INPUT_APIS = new EnumMap<>(UpstreamDatasetHolder.Dataset.class);
        static {
            INPUT_APIS.put(CONVERTER, VMSHollowInputAPI.class);
            INPUT_APIS.put(GATEKEEPER2, VMSHollowInputAPI.class);
        }

        public static Map<UpstreamDatasetHolder.Dataset, String> getNamespaces() {
            if(NetflixConfiguration.getEnvironmentEnum() == NetflixConfiguration.EnvironmentEnum.prod)
                return PROD_INPUT_NAMESPACES;
            else
                return TEST_INPUT_NAMESPACES;
        }

        /**
         * Returns a string that is used as the key to create input version attributes in the transformer output header
         * and metadata for all Cinder inputs.
         * NOTE: the attribute is computed based on enumerators in the {@code Dataset} enum instead of namespace so that
         * namespace changes are backwards compatible. The dot separator can not exist in the enum's enumerators.
         * @param dataset The dataset for which input version attribute key is to be computed
         * @return input version attribtue key. For eg.  "cinder.converter.input.version", "cinder.gatekeeper2.input.version", etc.
         */
        public static String getInputVersionAttribute(UpstreamDatasetHolder.Dataset dataset) {
            return "cinder." + dataset.toString().toLowerCase() + ".input.version";
        }
    }

    public void setDataset(Dataset key, UpstreamDataset dataset) {
        this.inputs.put(key, dataset);
    }

    @SuppressWarnings("unchecked")
    public <T extends UpstreamDataset> T getDataset(Dataset dataset) {
        return (T)inputs.get(dataset);
    }
}
