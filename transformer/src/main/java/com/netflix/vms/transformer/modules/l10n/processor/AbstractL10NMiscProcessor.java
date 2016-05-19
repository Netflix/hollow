package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

import java.util.Collection;

public abstract class AbstractL10NMiscProcessor<K> extends AbstractL10NProcessor<K>implements L10NMiscProcessor<K> {

    AbstractL10NMiscProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    /**
     * Process Resources and return the number of items processed
     */
    @Override
    public final int processResources() {
        Collection<K> inputs = getInputs();
        for (K input : inputs) {
            processInput(input);
        }
        return inputs.size();
    }

}
