package com.netflix.sunjeetsonboardingroot;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields = "uniq")
public class FooType {

    public int uniq;

    public FooType() {}

    public FooType(int uniq) {
        this.uniq = uniq;
    }
}
