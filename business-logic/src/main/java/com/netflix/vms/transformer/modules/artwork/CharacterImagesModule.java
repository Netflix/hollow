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
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.CharacterArtworkSourceHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.CharacterImages;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharacterImagesModule extends ArtWorkModule{
    
    private final HollowPrimaryKeyIndex characterArtworkIdx;

    public CharacterImagesModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super("Character", api, ctx, mapper, cycleConstants, indexer);
        this.characterArtworkIdx = indexer.getPrimaryKeyIndex(IndexSpec.CHARACTER_ARTWORK_SOURCE_BY_SOURCE_ID);
        allImagesAreVariableSize = true;
    }

    @Override
    public void transform() {
        /// short-circuit Fastlane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.CharacterImages) && ctx.getFastlaneIds() != null)
            return;

        Map<Integer, Set<Artwork>> artMap = new HashMap<>();
        for(CharacterArtworkSourceHollow artworkHollowInput : api.getAllCharacterArtworkSourceHollow()) {
            if(!artworkHollowInput._getIsFallback()) {
                processArtworkWithFallback(artMap, artworkHollowInput);
            }
        }

        for (Map.Entry<Integer, Set<Artwork>> entry : artMap.entrySet()) {
            Integer id = entry.getKey();
            CharacterImages images = new CharacterImages();
            images.id = id;
            images.artworks = createArtworkByTypeMap(entry.getValue());
            mapper.add(images);
        }
    }
    
    public void processArtworkWithFallback(Map<Integer, Set<Artwork>> artMap, CharacterArtworkSourceHollow artworkHollowInput) {
        String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
        HollowHashIndexResult derivativeSetMatches = artworkDerivativeSetIdx.findMatches(artworkHollowInput._getSourceFileId()._getValue());

        if(derivativeSetMatches != null) {
            int entityId = (int) artworkHollowInput._getCharacterId();
            ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
            Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
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
                int fallbackOrdinal = characterArtworkIdx.getMatchingOrdinal(fallbackSourceId._getValue());
                CharacterArtworkSourceHollow fallbackSource = api.getCharacterArtworkSourceHollow(fallbackOrdinal);
                processArtworkWithFallback(artMap, fallbackSource);
            }
        }
    }
}
