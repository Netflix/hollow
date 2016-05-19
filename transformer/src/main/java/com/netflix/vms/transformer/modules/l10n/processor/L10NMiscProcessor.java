package com.netflix.vms.transformer.modules.l10n.processor;

import java.util.Collection;

public interface L10NMiscProcessor<K> extends L10NProcessor<K> {
    /**
     * Return the inputs
     */
    Collection<K> getInputs();

    /**
     * Process Resources and return the number of items processed
     */
    int processResources();
}