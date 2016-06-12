package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.LocalizedCharacterHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class LocalizedCharacterProcessor extends AbstractL10NMiscProcessor<LocalizedCharacterHollow> {

    public LocalizedCharacterProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<LocalizedCharacterHollow> getInputs() {
        return api.getAllLocalizedCharacterHollow();
    }

    @Override
    public void processInput(LocalizedCharacterHollow input) {
        final int inputId = (int) input._getCharacterId();
        final String name = input._getAttributeName()._getValue();
        final String label = input._getLabel()._getValue();

        final String resourceId = L10nResourceIdLookup.getCharacterAttribResourceId(inputId, name, label);
        addL10NResources(resourceId, input._getTranslatedTexts());
    }
}