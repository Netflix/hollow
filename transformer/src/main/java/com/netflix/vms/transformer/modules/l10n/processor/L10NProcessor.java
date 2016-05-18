package com.netflix.vms.transformer.modules.l10n.processor;

public interface L10NProcessor {

    /**
     * Process Resources and return the number of items processed
     */
    int processResources();

    /**
     * Return the items added
     */
    int getItemsAdded();
}