package com.netflix.vms.transformer.common;

@FunctionalInterface
public interface VersionMinter {
    
    public long mintANewVersion();

}
