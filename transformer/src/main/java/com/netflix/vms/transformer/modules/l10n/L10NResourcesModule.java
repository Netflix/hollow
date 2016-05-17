package com.netflix.vms.transformer.modules.l10n;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.modules.l10n.processor.L10NProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.PersonAliasesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.PersonsProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.ShowMemberTypesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.StoriesSynopsesProcessor;

import java.util.ArrayList;
import java.util.List;

public class L10NResourcesModule extends AbstractTransformModule {
    private List<L10NProcessor> processorList = new ArrayList<>();

    public L10NResourcesModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);

        processorList.add(new PersonAliasesProcessor(api, ctx, mapper));
        processorList.add(new PersonsProcessor(api, ctx, mapper));
        processorList.add(new ShowMemberTypesProcessor(api, ctx, mapper));
        processorList.add(new ShowMemberTypesProcessor(api, ctx, mapper));
        processorList.add(new StoriesSynopsesProcessor(api, ctx, mapper));
    }

    @Override
    public void transform() {
        for (L10NProcessor processor : processorList) {
            processor.processResources();
        }
    }
}