package com.netflix.vms.transformer.logger;

import com.netflix.archaius.api.Config;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TransformerConfigLogger {
    
    public static void logProperties(TransformerConfig transformerConfig, Config config, TransformerLogger logger) {
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
            logger.error(LogTag.PropertyValue, "Unable to log property values", e);
        }
    }
    
    private static String getPropertyName(String methodName) {
        if(methodName.startsWith("get"))
            return "vms." + methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        if(methodName.startsWith("is"))
            return "vms." + methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        return "vms." + methodName;
    }

}
