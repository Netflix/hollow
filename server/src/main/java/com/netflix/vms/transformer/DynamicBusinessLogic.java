package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.DynamicLogicLoading;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.gutenberg.consumer.VersionMetadata;
import com.netflix.vms.transformer.common.api.BusinessLogicAPI;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;
import com.netflix.vms.transformer.logger.TransformerServerLogger;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.commons.io.FileUtils;

@Singleton
public class DynamicBusinessLogic implements Supplier<BusinessLogicAPI> {

    private final TransformerServerLogger logger;
    private CurrentBusinessLogicClassHolder currentLogicClass;

    @Inject
    public DynamicBusinessLogic(TransformerConfig config, GutenbergFileConsumer gutenberg,
            TransformerConfig transformerConfig, ElasticSearchClient esClient) throws Exception {

        this.logger = new TransformerServerLogger(transformerConfig, esClient);

        String dynamicLogicNamespace = config.getOverrideDynamicLogicJarNamespace();

        if(dynamicLogicNamespace == null)
            dynamicLogicNamespace = "vmstransformer-logic-" + config.getTransformerVip();

        List<VersionMetadata> versions = gutenberg.getVersions(dynamicLogicNamespace, 1);
        logger.info(DynamicLogicLoading, String.format(
                "Retrieved %d versions from dynamic logic namespace %s", versions.size(), dynamicLogicNamespace));

        File jarFile = gutenberg.getData(dynamicLogicNamespace, versions.get(0).getVersion());
        logger.info(DynamicLogicLoading, "Dynamic logic file downloaded to " + jarFile);

        loadLogic(jarFile, versions.get(0).getMetadata());
        gutenberg.subscribe(dynamicLogicNamespace, (newJarFile, metadata) -> {
            try {
                loadLogic(newJarFile, metadata);
            } catch (Exception e) {
                logger.error(DynamicLogicLoading, "Failed to update Business logic dynamically");
            }
        });
    }

    public CurrentBusinessLogicHolder getLogicAndMetadata() {
        CurrentBusinessLogicClassHolder current = currentLogicClass;
        return new CurrentBusinessLogicHolder(instantiateLogic(current), current.metadata);
    }

    @Override
    public BusinessLogicAPI get() {
        return instantiateLogic(currentLogicClass);
    }

    private BusinessLogicAPI instantiateLogic(CurrentBusinessLogicClassHolder classHolder) {
        try {
            return (BusinessLogicAPI) currentLogicClass.clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(DynamicLogicLoading, "Could not instantiate BusinessLogicAPI", e);
            throw new RuntimeException(e);
        }
    }

    private void loadLogic(File srcJar, Map<String, String> metadata) {
        try {
            /// copy the file to avoid potential overwrite while in use
            Path srcPath = srcJar.toPath();
            Path destPath = srcPath.resolveSibling(srcPath.getName(srcPath.getNameCount()-1) + "-" + UUID.randomUUID().toString());
            File destJar = destPath.toFile();
            FileUtils.copyFile(srcJar, destJar);

            ClassLoader currentLoader = new URLClassLoader(new URL[] { destJar.toURI().toURL() }, getClass().getClassLoader());
            Class<?> currentBusinessLogicClass = currentLoader.loadClass("com.netflix.vms.transformer.SimpleTransformer");

            this.currentLogicClass = new CurrentBusinessLogicClassHolder(currentBusinessLogicClass, metadata);
        } catch(Throwable th) {
            logger.error(DynamicLogicLoading, "Could not load dynamic business logic", th);
        }
    }

    private static class CurrentBusinessLogicClassHolder {
        private final Class<?> clazz;
        private final Map<String, String> metadata;

        public CurrentBusinessLogicClassHolder(Class<?> currentBusinessLogicClass, Map<String, String> metadata) {
            this.clazz = currentBusinessLogicClass;
            this.metadata = metadata;
        }
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