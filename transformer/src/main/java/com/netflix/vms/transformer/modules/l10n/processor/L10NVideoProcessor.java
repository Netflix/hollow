package com.netflix.vms.transformer.modules.l10n.processor;

import java.util.Collection;
import java.util.Set;

public interface L10NVideoProcessor<K> extends L10NProcessor<K> {

    /**
     * Return the inputs
     */
    Collection<K> getInputs(Set<Integer> videoSet);

    /**
     * Process Resources and return the number of items processed
     */
    int processResources(Set<Integer> videoSet);
}