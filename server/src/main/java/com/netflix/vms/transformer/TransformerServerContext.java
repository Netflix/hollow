package com.netflix.vms.transformer;

import java.util.function.Consumer;

import com.netflix.vms.transformer.common.PublicationHistory;
import com.netflix.vms.transformer.common.PublicationHistoryConsumer;
import com.netflix.vms.transformer.common.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerFiles;
import com.netflix.vms.transformer.common.TransformerLogger;
import com.netflix.vms.transformer.common.TransformerPlatformLibraries;

/**
 * Properties go here.
 *
 */
public class TransformerServerContext implements TransformerContext {

    /* dependencies */
    private final TransformerLogger logger;
    private final TransformerCassandraHelper poisonStatesHelper;
    private final TransformerCassandraHelper hollowValidationStats;
    private final TransformerCassandraHelper canaryResults;
    private final TransformerFiles files;
    private final TransformerPlatformLibraries platformLibraries;
    private final PublicationHistoryConsumer publicationHistoryConsumer;

    /* fields */
    private long now = System.currentTimeMillis();

    public TransformerServerContext(TransformerLogger logger,
            TransformerCassandraHelper poisonStatesHelper,
            TransformerCassandraHelper hollowValidationStats,
            TransformerCassandraHelper canaryResults,
            TransformerFiles files,
            TransformerPlatformLibraries platformLibraries,
            PublicationHistoryConsumer publicationHistoryConsumer) {
        this.logger = logger;
        this.poisonStatesHelper = poisonStatesHelper;
        this.hollowValidationStats = hollowValidationStats;
        this.canaryResults = canaryResults;
        this.files = files;
        this.platformLibraries = platformLibraries;
        this.publicationHistoryConsumer = publicationHistoryConsumer;
    }

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
    public TransformerCassandraHelper getPoisonStatesHelper() {
        return poisonStatesHelper;
    }

    @Override
    public TransformerCassandraHelper getValidationStatsCassandraHelper() {
        return hollowValidationStats;
    }

    @Override
    public TransformerCassandraHelper getCanaryResultsCassandraHelper() {
        return canaryResults;
    }

    @Override
    public TransformerFiles files() {
        return files;
    }

    @Override
    public  TransformerPlatformLibraries platformLibraries() {
        return platformLibraries;
    }

    @Override
    public Consumer<PublicationHistory> getPublicationHistoryConsumer() {
        return publicationHistoryConsumer;
    }
}
