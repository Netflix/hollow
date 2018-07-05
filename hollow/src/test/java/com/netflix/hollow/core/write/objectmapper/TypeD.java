package com.netflix.hollow.core.write.objectmapper;

public class TypeD {
    @HollowInline private final String inlinedString;

    public TypeD(String inlinedString) {
        this.inlinedString = inlinedString;
    }

    public String getInlinedString() {
        return inlinedString;
    }
}
