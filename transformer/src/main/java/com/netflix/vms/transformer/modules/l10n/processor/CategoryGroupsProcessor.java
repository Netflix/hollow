package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.CategoryGroupsHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.modules.l10n.L10nResourceIdLookup;

import java.util.Collection;

public class CategoryGroupsProcessor extends AbstractL10NProcessor<CategoryGroupsHollow> {

    public CategoryGroupsProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        super(api, ctx, mapper);
    }

    @Override
    public Collection<CategoryGroupsHollow> getInputs() {
        return api.getAllCategoryGroupsHollow();
    }

    @Override
    public void processInput(CategoryGroupsHollow input) {
        final int inputId = (int) input._getCategoryGroupId();

        final String resourceId = L10nResourceIdLookup.getCategoryGroupNameID(inputId);
        addL10NResources(resourceId, input._getCategoryGroupName());
    }
}