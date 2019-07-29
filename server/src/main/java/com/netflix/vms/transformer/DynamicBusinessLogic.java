package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.DynamicLogicLoading;

import com.google.inject.Singleton;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.gutenberg.consumer.VersionMetadata;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.api.BusinessLogicAPI;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import javax.inject.Inject;

@Singleton
public class DynamicBusinessLogic implements Supplier<BusinessLogicAPI> {

    private final GutenbergFileConsumer gutenberg;
    private final TransformerServerLogger logger;

    private ServiceLoader<BusinessLogicAPI> loader;
    private CurrentBusinessLogicHolder currentBusinessLogicHolder;

    @Inject
    public DynamicBusinessLogic(TransformerConfig config, GutenbergFileConsumer gutenberg,
            TransformerConfig transformerConfig, ElasticSearchClient esClient) throws Exception {
        this.gutenberg = gutenberg;
        this.logger = new TransformerServerLogger(transformerConfig, esClient);

        String dynamicLogicNamespace = config.getOverrideDynamicLogicJarNamespace();

        if(dynamicLogicNamespace == null)
            dynamicLogicNamespace = "vmstransformer-logic-" + config.getTransformerVip();

        List<VersionMetadata> versions = gutenberg.getVersions(dynamicLogicNamespace, 1);

        File jarFile = gutenberg.getData(dynamicLogicNamespace, versions.get(0).getVersion());

        loadLogic(jarFile, versions.get(0).getMetadata());

        gutenberg.subscribe(dynamicLogicNamespace, (newJarFile, metadata) -> {
            try {
                loadLogic(newJarFile, metadata);
            } catch (Exception e) {
                logger.error(DynamicLogicLoading, "Failed to update Business logic dynamically");
            }
        });
    }

    @Override
    public BusinessLogicAPI get() {
        return currentBusinessLogicHolder.getLogic();
    }

    public CurrentBusinessLogicHolder getLogicAndMetadata() {
        return currentBusinessLogicHolder;
    }

    private void loadLogic(File jarFile, Map<String, String> metadata) throws Exception {
        BusinessLogicAPI currentBusinessLogic = null;
        try {
            ClassLoader jarLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() });

            if (loader == null)
                loader = ServiceLoader.load(BusinessLogicAPI.class, jarLoader);    // gets instance of BusinessLogicAPI
            else
                loader.reload();    // discovers newly installed implementations of BusinessLogicAPI

            Iterator<BusinessLogicAPI> iterator = loader.iterator();
            while (iterator.hasNext()) {
                currentBusinessLogic = iterator.next();
                logger.info(DynamicLogicLoading,
                        "Installed Service Provider: " + iterator.next().toString());
            }
        } catch (ServiceConfigurationError | MalformedURLException e) {
            logger.error(DynamicLogicLoading,
                    "Failed to install business logic service provider, error is unrecoverable");
            throw e;
        }
        this.currentBusinessLogicHolder = new CurrentBusinessLogicHolder(currentBusinessLogic, metadata);
    }

    public static class CurrentBusinessLogicHolder {
        private final BusinessLogicAPI logic;
        private final Map<String, String> metadata;

        public CurrentBusinessLogicHolder(BusinessLogicAPI logic, Map<String, String> metadata) {
            this.logic = logic;
            this.metadata = metadata;
        }

        public BusinessLogicAPI getLogic() {
            return logic;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }
    }


}