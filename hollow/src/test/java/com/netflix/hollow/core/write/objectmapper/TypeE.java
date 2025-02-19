package com.netflix.hollow.core.write.objectmapper;

import java.util.Map;

public class TypeE {
    private Map<SubType, Integer> map;

    public TypeE(
            Map<SubType, Integer> map
    ) {
        this.map = map;
    }

    public Map<SubType, Integer> getMap() {
        return map;
    }

    public static class SubType {
        private String name;
        private Integer year;

        public SubType(String name, Integer year) {
            this.name = name;
            this.year = year;
        }
    }
}
