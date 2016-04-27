package com.netflix.vms.transformer.logger;

import com.netflix.logging.LogManager;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.logging.ILog;

public class TransformerServerLogger implements TransformerLogger {

    private final ILog LOGGER = LogManager.getLogger("Transformer");

    @Override
    public void info(String messageType, String message) {
        LOGGER.info(messageType + ": " + message);
    }

    @Override
    public void warn(String messageType, String message) {
        LOGGER.warn(messageType + ": " + message);
    }

    @Override
    public void error(String messageType, String message) {
        LOGGER.error(messageType + ": " + message);
    }

    @Override
    public void error(String messageType, String message, Throwable th) {
        LOGGER.error(messageType + ": " + message, th);
    }

}
