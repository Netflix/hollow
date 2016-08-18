package com.netflix.vms.transformer.modules.l10n.processor;

import java.util.Collection;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.PersonCharacterResourceHollow;
import com.netflix.vms.transformer.hollowinput.TranslatedTextHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

public class PersonCharacterProcessor extends AbstractL10NMiscProcessor<PersonCharacterResourceHollow> {

    public PersonCharacterProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<PersonCharacterResourceHollow> getInputs() {
        return api.getAllPersonCharacterResourceHollow();
    }

    @Override
    public void processInput(PersonCharacterResourceHollow input) {
        final int inputId = (int) input._getId();
        final String prefix = input._getPrefix()._getValue();

        TranslatedTextHollow value = input._getCn();
        if (value != null) {
            String resourceId = L10nResourceIdLookup.getGenericResourceId(inputId, prefix, "cn");
            addL10NResources(resourceId, value);
        }
    }
}