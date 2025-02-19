package com.netflix.hollow.core.write.objectmapper;

import java.util.List;
import java.util.Map;

public class TypeE {
    private Map<SubType, Integer> map;
    private Map<List<SubType>, Integer> mapWithList;
    private Map<String, List<SubType>> mapWithString;

    public TypeE(
            Map<SubType, Integer> map,
            Map<List<SubType>, Integer> mapWithList,
            Map<String, List<SubType>> mapWithString
    ) {
        this.map = map;
        this.mapWithList = mapWithList;
        this.mapWithString = mapWithString;
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
