package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.LanguagesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class LanguagesProcessor extends AbstractL10NMiscProcessor<LanguagesHollow> {

    public LanguagesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<LanguagesHollow> getInputs() {
        return api.getAllLanguagesHollow();
    }

    @Override
    public void processInput(LanguagesHollow input) {
        final int inputId = (int) input._getLanguageId();

        final String resourceId = L10nResourceIdLookup.getLanguageNameID(inputId);
        addL10NResources(resourceId, input._getName());
    }
}