package com.netflix.vms.transformer.modules.l10n;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.VideoHierarchy;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.modules.l10n.processor.EpisodesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.L10NVideoProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.LocalizedMetadataProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.MoviesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.StoriesSynopsesProcessor;
import com.netflix.vms.transformer.modules.l10n.processor.VideoRatingsProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class L10NVideoResourcesModule extends AbstractTransformModule {
    private List<L10NVideoProcessor<?>> processorList = new ArrayList<>();

    public L10NVideoResourcesModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);

        processorList.add(new EpisodesProcessor(api, ctx, mapper, indexer));
        processorList.add(new MoviesProcessor(api, ctx, mapper, indexer));
        processorList.add(new LocalizedMetadataProcessor(api, ctx, mapper, indexer));
        processorList.add(new StoriesSynopsesProcessor(api, ctx, mapper, indexer));
        processorList.add(new VideoRatingsProcessor(api, ctx, mapper, indexer));
    }

    public void transform(Map<String, Set<VideoHierarchy>> showHierarchiesByCountry, Set<Integer> extraVideoIds) {
        Set<Integer> videoSet = new HashSet<>(extraVideoIds);

        if (showHierarchiesByCountry != null) {
            for (Map.Entry<String, Set<VideoHierarchy>> entry : showHierarchiesByCountry.entrySet()) {
                for(VideoHierarchy hierarchy : entry.getValue()) {
                    videoSet.addAll(hierarchy.getAllIds());
                }
            }
        }

        for (L10NVideoProcessor<?> processor : processorList) {
            processor.processResources(videoSet);
        }

    }

    @Override
    public void transform() {
        throw new UnsupportedOperationException("Use transform(Map<String, ShowHierarchy> showHierarchiesByCountry)");
    }
}