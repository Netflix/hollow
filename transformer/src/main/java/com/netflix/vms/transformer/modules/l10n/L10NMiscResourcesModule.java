package com.netflix.vms.transformer.modules.l10n;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.modules.l10n.processor.AltGenresProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.AssetMetaDatasProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.AwardsProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.CategoriesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.CategoryGroupsProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.CertificationSystemsProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.CharactersProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.FestivalsProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.L10NMiscProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.LanguagesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.LocalizedCharacterProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.PersonAliasesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.PersonsProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.ShowMemberTypesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.TurboCollectionsProcessor;

import java.util.ArrayList;
import java.util.List;

public class L10NMiscResourcesModule extends AbstractTransformModule {
    private List<L10NMiscProcessor<?>> processorList = new ArrayList<>();

    public L10NMiscResourcesModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);

        processorList.add(new AltGenresProcessor(api, ctx, mapper));
        processorList.add(new AssetMetaDatasProcessor(api, ctx, mapper));
        processorList.add(new AwardsProcessor(api, ctx, mapper));
        processorList.add(new CategoriesProcessor(api, ctx, mapper));
        processorList.add(new CategoryGroupsProcessor(api, ctx, mapper));
        processorList.add(new CertificationSystemsProcessor(api, ctx, mapper));
        processorList.add(new CharactersProcessor(api, ctx, mapper));
        processorList.add(new FestivalsProcessor(api, ctx, mapper));
        processorList.add(new LanguagesProcessor(api, ctx, mapper));
        processorList.add(new LocalizedCharacterProcessor(api, ctx, mapper));
        processorList.add(new PersonAliasesProcessor(api, ctx, mapper));
        processorList.add(new PersonsProcessor(api, ctx, mapper));
        processorList.add(new ShowMemberTypesProcessor(api, ctx, mapper));
        processorList.add(new TurboCollectionsProcessor(api, ctx, mapper));

    }

    @Override
    public void transform() {
        for (L10NMiscProcessor<?> processor : processorList) {
            long start = System.currentTimeMillis();

            String processorName = processor.getClass().getSimpleName();
            int itemsProcessed = processor.processResources();
            int itemsAdded = processor.getItemsAdded();

            long duration = System.currentTimeMillis() - start;
            ctx.getLogger().info(LogTag.L10NProcessing, String.format("processorName=%s, itemsProcessed=%s, itemsAdded=%s, duraction=%s", processorName, itemsProcessed, itemsAdded, duration));
        }
    }
}