package com.netflix.vms.transformer.modules.l10n.processor;

import com.netflix.hollow.index.HollowHashIndex;
import com.netflix.hollow.index.HollowHashIndexResult;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class AbstractL10NVideoProcessor<K> extends AbstractL10NProcessor<K>implements L10NVideoProcessor<K> {
    private final HollowPrimaryKeyIndex l10nFeedPrimaryKeyIndex;
    private final HollowHashIndex l10nFeedHashIndex;
    private final boolean isPrimaryKeyIndex;

    AbstractL10NVideoProcessor(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer, IndexSpec l10nIndexSpec) {
        super(api, ctx, mapper);

        this.isPrimaryKeyIndex = IndexSpec.IndexType.PRIMARY_KEY.equals(l10nIndexSpec.getIndexType());
        this.l10nFeedPrimaryKeyIndex = isPrimaryKeyIndex ? indexer.getPrimaryKeyIndex(l10nIndexSpec) : null;
        this.l10nFeedHashIndex = isPrimaryKeyIndex ? null : indexer.getHashIndex(l10nIndexSpec);
    }

    protected abstract K getDataForOrdinal(int ordinal);

    @Override
    public Collection<K> getInputs(Set<Integer> videoSet) {
        if (isPrimaryKeyIndex) {
            return getInputs(videoSet, l10nFeedPrimaryKeyIndex);
        } else {
            return getInputs(videoSet, l10nFeedHashIndex);
        }
    }

    protected Collection<K> getInputs(Set<Integer> videoSet, HollowHashIndex hashIndex) {
        List<K> inputs = new ArrayList<>();

        for (Integer videoId : videoSet) {
            HollowHashIndexResult matches = hashIndex.findMatches((long) videoId);
            if (matches != null) {
                HollowOrdinalIterator iter = matches.iterator();
                int ordinal = iter.next();
                while (ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    K input = getDataForOrdinal(ordinal);
                    inputs.add(input);
                    ordinal = iter.next();
                }
            }
        }

        return inputs;
    }

    protected Collection<K> getInputs(Set<Integer> videoSet, HollowPrimaryKeyIndex hashIndex) {
        List<K> inputs = new ArrayList<>();

        for (long videoId : videoSet) {
            int ordinal = hashIndex.getMatchingOrdinal(videoId);
            if (ordinal != -1) {
                K input = getDataForOrdinal(ordinal);
                inputs.add(input);
            }
        }

        return inputs;
    }

    /**
     * Process Resources and return the number of items processed
     */
    @Override
    public final int processResources(Set<Integer> videoSet) {
        Collection<K> inputs = getInputs(videoSet);
        for (K input : inputs) {
            processInput(input);
        }
        return inputs.size();
    }
}