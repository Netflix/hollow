package com.netflix.vms.transformer.config;

import com.netflix.archaius.ConfigProxyFactory;
import com.netflix.archaius.api.Config;
import com.netflix.archaius.api.annotations.Configuration;
import com.netflix.archaius.config.MapConfig;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class FrozenTransformerConfigFactory {
    
    private final Config config;
    private final String propertyPrefix;
    
    public FrozenTransformerConfigFactory(Config config) {
        this.config = config;
        this.propertyPrefix = TransformerConfig.class.getAnnotation(Configuration.class).prefix() + ".";
    }
    
    
    public TransformerConfig createStaticConfig(TransformerLogger logger) {
        String propertiesString = getPropertiesString();
        
        Properties props = new Properties();
        try {
            props.load(new StringReader(propertiesString));
        } catch (IOException e) {
            logger.error(LogTag.ConfigurationFailure, "Failed to parse properties String: " + propertiesString);
        }
        
        TransformerConfig transformerConfig = new ConfigProxyFactory(new MapConfig(props)).newProxy(TransformerConfig.class);
        logProperties(transformerConfig, logger);
        return transformerConfig;
    }

    private String getPropertiesString() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = config.getKeys(propertyPrefix);
        
        while(iter.hasNext()) {
            String key = iter.next();
            builder.append(key).append("=").append(config.getString(key)).append("\n");
        }

        return builder.toString();
    }    
    
    private void logProperties(TransformerConfig transformerConfig, TransformerLogger logger) {
        Set<String> loggedKeys = new HashSet<>();
        
        try {
            for(Method m : TransformerConfig.class.getDeclaredMethods()) {
                String methodName = m.getName();
                String propertyName = getPropertyName(methodName);
                
                if(m.getParameterCount() == 0) {
                    Object value = m.invoke(transformerConfig);
                    logger.info(LogTag.PropertyValue, "key=" + propertyName + " value=" + value);
                } else {
                    Iterator<String> keyIter = config.getKeys(propertyName);
                    while(keyIter.hasNext()) {
                        String key = keyIter.next();
                        if(loggedKeys.add(key)) {
                            String value = config.getString(key);
                            logger.info(LogTag.PropertyValue, "key=" + key + " value=" + value);
                        }
                    }
                }
            }
        } catch(Exception e) {
            logger.error(LogTag.ConfigurationFailure, "Unable to log property values", e);
        }
    }
    
    private String getPropertyName(String methodName) {
        if(methodName.startsWith("get"))
            return propertyPrefix + methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        if(methodName.startsWith("is"))
            return propertyPrefix + methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        return propertyPrefix + methodName;
    }

}
