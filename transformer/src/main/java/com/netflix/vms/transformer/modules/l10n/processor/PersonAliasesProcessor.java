package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.PersonAliasesHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class PersonAliasesProcessor extends AbstractL10NProcessor<PersonAliasesHollow> {

    public PersonAliasesProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<PersonAliasesHollow> getInputs() {
        return api.getAllPersonAliasesHollow();
    }

    @Override
    public void processInput(PersonAliasesHollow input) {
        final int inputId = (int) input._getAliasId();

        final String resourceId = L10nResourceIdLookup.getPersonAliasID(inputId);
        addL10NResources(resourceId, input._getName());
    }
}