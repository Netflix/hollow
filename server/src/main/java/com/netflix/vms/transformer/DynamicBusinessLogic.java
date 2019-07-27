package com.netflix.vms.transformer;

import com.google.inject.Singleton;
import com.netflix.gutenberg.consumer.GutenbergFileConsumer;
import com.netflix.gutenberg.consumer.VersionMetadata;
import com.netflix.vms.transformer.common.BusinessLogic;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DynamicBusinessLogic implements Supplier<BusinessLogic> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicBusinessLogic.class);

    private final GutenbergFileConsumer gutenberg;

    private CurrentBusinessLogicClassHolder currentLogicClass;

    @Inject
    public DynamicBusinessLogic(TransformerConfig config, GutenbergFileConsumer gutenberg) {
        this.gutenberg = gutenberg;

        String dynamicLogicNamespace = config.getOverrideDynamicLogicJarNamespace();

        if(dynamicLogicNamespace == null)
            dynamicLogicNamespace = "vmstransformer-logic-" + config.getTransformerVip();

        List<VersionMetadata> versions = gutenberg.getVersions(dynamicLogicNamespace, 1);

        File data = gutenberg.getData(dynamicLogicNamespace, versions.get(0).getVersion());
        loadLogic(data, versions.get(0).getMetadata());

        gutenberg.subscribe(dynamicLogicNamespace, (jarFile, metadata) -> {
            loadLogic(jarFile, metadata);
        });
    }

    @Override
    public BusinessLogic get() {
        return instantiateLogic(currentLogicClass);
    }

    public CurrentBusinessLogicHolder getLogicAndMetadata() {
        CurrentBusinessLogicClassHolder current = currentLogicClass;
        return new CurrentBusinessLogicHolder(instantiateLogic(current), current.metadata);
    }

    private BusinessLogic instantiateLogic(CurrentBusinessLogicClassHolder classHolder) {
        try {
            return (BusinessLogic) currentLogicClass.clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not instantiate BusinessLogic", e);
            throw new RuntimeException(e);
        }
    }

    private void loadLogic(File jarFile, Map<String, String> metadata) {    // SNAP: Replace with a Service provider interface
        try {
            /// copy the file to avoid potential overwrite while in use
            int random = Math.abs(new Random().nextInt());
            FileUtils.copyFile(jarFile, new File(jarFile.getParentFile(), "dynamic-logic-" + Integer.toHexString(random) + ".jar"));

            ClassLoader currentLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, getClass().getClassLoader());

            Class<?> currentBusinessLogicClass = currentLoader.loadClass("com.netflix.vms.transformer.SimpleTransformer");

            this.currentLogicClass = new CurrentBusinessLogicClassHolder(currentBusinessLogicClass, metadata);
        } catch(Throwable th) {
            logger.error("Could not load dynamic business logic", th);
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
        private final BusinessLogic logic;
        private final Map<String, String> metadata;

        public CurrentBusinessLogicHolder(BusinessLogic logic, Map<String, String> metadata) {
            this.logic = logic;
            this.metadata = metadata;
        }

        public BusinessLogic getLogic() {
            return logic;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }
    }


}