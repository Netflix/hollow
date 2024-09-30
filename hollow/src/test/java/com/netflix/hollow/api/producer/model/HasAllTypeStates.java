package com.netflix.hollow.api.producer.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HasAllTypeStates {
    CustomReferenceType customReferenceType;
    Set<String> setOfStrings;
    List<Integer> listOfInt;
    Map<String, Long> mapOfStringToLong;

    public HasAllTypeStates(CustomReferenceType customReferenceType, Set<String> setOfStrings, List<Integer> listOfInt, Map<String, Long> mapOfStringToLong) {
        this.customReferenceType = customReferenceType;
        this.setOfStrings = setOfStrings;
        this.listOfInt = listOfInt;
        this.mapOfStringToLong = mapOfStringToLong;
    }
}
