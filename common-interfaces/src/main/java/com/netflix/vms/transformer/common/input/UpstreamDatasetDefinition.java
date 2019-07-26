package com.netflix.vms.transformer.common.input;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.netflix.config.NetflixConfiguration;
import java.util.EnumMap;
import java.util.Map;

public class UpstreamDatasetDefinition {

    public static final String INPUT_VERSION_KEY_PREFIX = "input.cinder.version.";

    /**
     * Input datasets are enumerated here.
     */
    public enum DatasetIdentifier {

        CONVERTER,
        GATEKEEPER2,
        TOPN;
        //TODO: enable me once we can turn on the new data set including follow vip functionality
//        OSCAR;

        public static final Map<DatasetIdentifier, String> PROD_INPUT_NAMESPACES = new EnumMap<>(DatasetIdentifier.class);
        public static final Map<DatasetIdentifier, String> TEST_INPUT_NAMESPACES = new EnumMap<>(DatasetIdentifier.class);

        static {
            PROD_INPUT_NAMESPACES.put(CONVERTER, "vmsconverter-muon");
            PROD_INPUT_NAMESPACES.put(GATEKEEPER2, "gatekeeper2_status_prod");
            PROD_INPUT_NAMESPACES.put(TOPN, "vms.popularViewables.topN");

            TEST_INPUT_NAMESPACES.put(CONVERTER, "vmsconverter-muon");
            TEST_INPUT_NAMESPACES.put(GATEKEEPER2, "gatekeeper2_status_test");
            TEST_INPUT_NAMESPACES.put(TOPN, "vms.popularViewables.topN");

            //
            // Cinder inputs to Converter that need to be migrated to transformer:
            //
            // .put(SHOW_SEASON_EPISODE, "cinder.oscar.memento.showSeasonEpisode");
            // .put(OSCAR_VIDEO_GENERAL, "cinder.oscar.memento.videoGeneral");  // NOTE: requires Hollow incremental production
            // .put(CUPTOKENRECORDS_V3, "cuptokenrecords.v3");
            // .put(MCL_EARLIEST_DATE, "movie-country-language-earliestdate-namespace");
            // .put(OSCAR_VIDEO_DATE, "cinder.oscar.memento.videoDate");
            // .put(AWARD, "cinder.award.award");
            // .put(VIDEO_AWARD, "cinder.award.videoAward");
            // .put(OSCAR_VIDEO_TYPE, "cinder.oscar.memento.videoType");
            // .put(OSCAR_COUNTRY_LABEL, "cinder.oscar.memento.showCountryLabel");
            // .put(OSCAR_LOCALIZED_METADATA, "cinder.oscar.memento.localizedMetaData");
            // .put(OSCAR_PERSON_VIDEO, "cinder.oscar.memento.personVideo");
            // .put(OSCAR_ROLLOUT, "cinder.oscar.memento.rollout");
            // .put(OSCAR_SUPPLEMENTAL, "cinder.oscar.memento.supplemental");
            // .put(PACKAGE_DEAL_COUNTRY, "packageDealCountry-feed");
            // .put(MCE_IMAGE_V3, "mcevmsfeeds.image.v3");
            // .put(EXHIBIT_DEAL_ATTRIBUTE_V1, "exhibit.deal.attribute.v1");
        }
    }

    public static class UpstreamDatasetConfig {
        public static Map<DatasetIdentifier, String> getNamespaces() {
            return getNamespacesforEnv(NetflixConfiguration.getEnvironmentEnum() == NetflixConfiguration.EnvironmentEnum.prod);
        }

        // For tooling that seeks test/prod data from dev boxes
        public static Map<DatasetIdentifier, String> getNamespacesforEnv(boolean isProd) {
            if(isProd)
                return DatasetIdentifier.PROD_INPUT_NAMESPACES;
            else
                return DatasetIdentifier.TEST_INPUT_NAMESPACES;
        }

        public static DatasetIdentifier lookupDatasetForNamespace(String namespace, boolean isProd) {
            final BiMap<DatasetIdentifier, String> namespaces = HashBiMap.create(getNamespacesforEnv(isProd));
            return namespaces.inverse().get(namespace);
        }

        public static DatasetIdentifier lookupDatasetForNamespaceInCurrentEnv(String namespace) {
            return lookupDatasetForNamespace(namespace, NetflixConfiguration.getEnvironment().equals("prod"));
        }

        /**
         * Returns a the key used to identify input version attributes in the transformer output header and metadata.
         * There is an entry for every Cinder input into the transformer. The key contains Cinder namespace which itself
         * may contain dot characters.
         * @param datasetIdentifier The datasetIdentifier for which input version attribute key is to be computed.
         * @return input version attribute key. For eg.  "input.cinder.version.vmsconverter-muon".
         */
        public static String getInputVersionAttribute(DatasetIdentifier datasetIdentifier) {
            return INPUT_VERSION_KEY_PREFIX + getNamespaces().get(datasetIdentifier);
        }
    }
}
