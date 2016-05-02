package com.netflix.vms.transformer;

import java.util.function.Consumer;

import com.netflix.vms.transformer.common.PublicationHistory;
import com.netflix.vms.transformer.common.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;
import com.netflix.vms.transformer.io.LZ4VMSTransformerFiles;

public class SimpleTransformerContext implements TransformerContext {

    private final TransformerLogger logger;
    private final TransformerFiles files;

    SimpleTransformerContext() {
        this(new SysoutTransformerLogger(), new LZ4VMSTransformerFiles());
    }

    SimpleTransformerContext(TransformerLogger logger, TransformerFiles files) {
        this.logger = logger;
        this.files = files;
    }

    private long now;

    @Override
    public void setNowMillis(long now) {
        this.now = now;
    }

    @Override
    public long getNowMillis() {
        return now;
    }

    @Override
    public TransformerLogger getLogger() {
        return logger;
    }

    @Override
    public TransformerCassandraHelper getCanaryResultsCassandraHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public TransformerCassandraHelper getValidationStatsCassandraHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public TransformerCassandraHelper getPoisonStatesHelper() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public TransformerFiles files() {
        return files;
    }

    @Override
    public TransformerPlatformLibraries platformLibraries() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }

    @Override
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        throw new UnsupportedOperationException("simple transformer doesn't publish");
    }
}
