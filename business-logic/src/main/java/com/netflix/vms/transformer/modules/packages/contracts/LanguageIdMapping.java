package com.netflix.vms.transformer.modules.packages.contracts;

import java.io.InputStream;

import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class LanguageIdMapping {
    
    private static final Map<String, Integer> idMapping = new HashMap<>();
    
    static {
        Properties props = new Properties();
        try (InputStream is = LanguageIdMapping.class.getResourceAsStream("/bcp47-language-id-mapping.properties")) {
            props.load(is);
            
            for(Map.Entry<Object, Object> entry : props.entrySet()) {
                String languageCode = (String)entry.getKey();
                Integer languageId = Integer.parseInt((String)entry.getValue());
                idMapping.put(languageCode, languageId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Integer getLanguageId(String languageCode) {
        Integer code = idMapping.get(languageCode);
        if(code == null)
            return Integer.valueOf(0);
        return code;
    }

}
