package com.netflix.vms.transformer.modules.artwork;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.MissingLocaleForArtwork;

import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.PersonArtworkSourceHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.PersonImages;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonImagesModule extends ArtWorkModule {

    private final HollowPrimaryKeyIndex personArtworkIdx;
    
    public PersonImagesModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super("Person", api, ctx, mapper, cycleConstants, indexer);
        this.personArtworkIdx = indexer.getPrimaryKeyIndex(IndexSpec.PERSON_ARTWORK_SOURCE_BY_SOURCE_ID);
    }

    @Override
    public void transform() {
        /// short-circuit Fastlane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.PersonImages) && ctx.getFastlaneIds() != null)
            return;

        Map<Integer, Set<Artwork>> artMap = new HashMap<>();
        for(PersonArtworkSourceHollow artworkHollowInput : api.getAllPersonArtworkSourceHollow()) {
            if(!artworkHollowInput._getIsFallback()) {
                Set<ArtworkLocaleHollow> locales = getLocalTerritories(artworkHollowInput._getLocales());
                processArtworkWithFallback(artMap, artworkHollowInput, locales);
            }
        }

        for (Map.Entry<Integer, Set<Artwork>> entry : artMap.entrySet()) {
            Integer id = entry.getKey();
            PersonImages images = new PersonImages();
            images.id = id;
            images.artworks = createArtworkByTypeMap(entry.getValue());
            mapper.add(images);
        }
    }

    private void processArtworkWithFallback(Map<Integer, Set<Artwork>> artMap, PersonArtworkSourceHollow artworkHollowInput, Set<ArtworkLocaleHollow> localeSet) {
        String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
        HollowHashIndexResult derivativeSetMatches = artworkDerivativeSetIdx.findMatches(artworkHollowInput._getSourceFileId()._getValue());
            
        if(derivativeSetMatches != null) {
            int entityId = (int) artworkHollowInput._getPersonId();
            if(localeSet.isEmpty()) {
                ctx.getLogger().error(MissingLocaleForArtwork, "Missing artwork locale for {} with id={}; data will be dropped.", entityType, entityId);
                return;
            }
            
            int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
            int seqNum = (int) artworkHollowInput._getSeqNum();
            ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();

            Set<Artwork> artworkSet = getArtworkSet(entityId, artMap);
            transformArtworks(entityId, sourceFileId, ordinalPriority, seqNum, attributes, derivativeSetMatches, localeSet, artworkSet);
        } else {
            StringHollow fallbackSourceId = artworkHollowInput._getFallbackSourceFileId();
            if(fallbackSourceId != null) {
                int fallbackOrdinal = personArtworkIdx.getMatchingOrdinal(fallbackSourceId._getValue());
                PersonArtworkSourceHollow fallbackSource = api.getPersonArtworkSourceHollow(fallbackOrdinal);
                processArtworkWithFallback(artMap, fallbackSource, localeSet);
            }
        }
    }
}
