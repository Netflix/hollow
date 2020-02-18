package com.netflix.sunjeetsonboardingroot;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;

@HollowPrimaryKey(fields = "bar")
public class BarType {

    public int bar;

    public BarType() {}

    public BarType(int bar) {
        this.bar = bar;
    }
}
