package com.netflix.vms.transformer.modules;

public interface TransformModule {

    public String getName();

    public void transform() throws Throwable;
}
