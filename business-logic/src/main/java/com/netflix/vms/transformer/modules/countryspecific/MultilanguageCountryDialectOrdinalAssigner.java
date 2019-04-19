package com.netflix.vms.transformer.modules.countryspecific;

import java.util.concurrent.ConcurrentHashMap;

public class MultilanguageCountryDialectOrdinalAssigner {
    
    private final ConcurrentHashMap<String, DialectOrdinalMap> map = new ConcurrentHashMap<>();
    
    public int getDialectOrdinal(String language, String dialect) {
        if(language.equals(dialect))
            return 0;
        
        DialectOrdinalMap dialectOrdinalMap = map.get(language);
        if(dialectOrdinalMap == null) {
            dialectOrdinalMap = new DialectOrdinalMap();
            DialectOrdinalMap existingMap = map.putIfAbsent(language, dialectOrdinalMap);
            if(existingMap != null)
                dialectOrdinalMap = existingMap;
        }
        
        return dialectOrdinalMap.getOrdinal(dialect); 
    }
    
    private class DialectOrdinalMap {
        private final ConcurrentHashMap<String, Integer> dialectOrdinalMap = new ConcurrentHashMap<>();
        
        public int getOrdinal(String dialect) {
            Integer ordinal = dialectOrdinalMap.get(dialect);
            if(ordinal != null)
                return ordinal.intValue();
            
            return addOrdinal(dialect);
        }
        
        private synchronized int addOrdinal(String dialect) {
            Integer ordinal = dialectOrdinalMap.get(dialect);
            if(ordinal != null)
                return ordinal.intValue();

            int nextOrdinal = dialectOrdinalMap.size() + 1;
            dialectOrdinalMap.put(dialect, nextOrdinal);
            return nextOrdinal;
        }
    }
    
    
}
