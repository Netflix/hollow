package com.netflix.vms.transformer.common.input;

public class UpstreamDatasetDefinition {

    public static final String INPUT_VERSION_KEY_PREFIX = "input.cinder.version.";

    public static class UpstreamDatasetConfig {

        /**
         * Returns a the key used to identify input version attributes in the transformer output header and metadata.
         * There is an entry for every Cinder input into the transformer. The key contains Cinder namespace which itself
         * may contain dot characters.
         * @param namespace The namespace for which input version attribute key is to be computed.
         * @return input version attribute key. For eg.  "input.cinder.version.vmsconverter-muon".
         */
        public static String getInputVersionAttribute(String namespace) {
            return INPUT_VERSION_KEY_PREFIX + namespace;
        }
    }
}
