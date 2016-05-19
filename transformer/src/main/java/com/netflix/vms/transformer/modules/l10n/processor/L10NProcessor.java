package com.netflix.vms.transformer.modules.l10n.processor;

import java.util.Collection;

public interface L10NProcessor<K> {

    /**
     * Return the inputs
     */
    Collection<K> getInputs();

    void processInput(K input);

    /**
     * Process Resources and return the number of items processed
     */
    int processResources();

    /**
     * Return the items added
     */
    int getItemsAdded();
}