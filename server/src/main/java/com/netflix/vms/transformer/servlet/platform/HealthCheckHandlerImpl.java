package com.netflix.vms.transformer.servlet.platform;

public class HealthCheckHandlerImpl implements com.netflix.karyon.spi.HealthCheckHandler {

    @Override
    public int getStatus() {
        return 200;
    }
}