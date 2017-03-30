package com.netflix.vms.transformer.modules.artwork;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.MissingLocaleForArtwork;

import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.OutputTypeConfig;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.CharacterArtworkHollow;
import com.netflix.vms.transformer.hollowinput.IPLDerivativeGroupHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.CharacterImages;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharacterImagesModule extends ArtWorkModule{

    public CharacterImagesModule(VMSHollowInputAPI api, TransformerContext ctx, CycleConstants cycleConstants, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super("Character", api, ctx, mapper, cycleConstants, indexer);
        allImagesAreVariableSize = true;
    }

    @Override
    public void transform() {
        /// short-circuit Fastlane
        if (OutputTypeConfig.FASTLANE_EXCLUDED_TYPES.contains(OutputTypeConfig.CharacterImages) && ctx.getFastlaneIds() != null)
            return;

        Map<Integer, Set<Artwork>> descMap = new HashMap<>();
        for(CharacterArtworkHollow artworkHollowInput : api.getAllCharacterArtworkHollow()) {
            int entityId = (int) artworkHollowInput._getCharacterId();
            ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
            Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
            if(localeSet.isEmpty()) {
                ctx.getLogger().error(MissingLocaleForArtwork, "Missing artwork locale for {} with id={}; data will be dropped.", entityType, entityId);
                continue;
            }

            String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
            int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
            int seqNum = (int) artworkHollowInput._getSeqNum();
            ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();
            HollowHashIndexResult derivativeSetMatches = artworkDerivativeSetIdx.findMatches(artworkHollowInput._getSourceFileId()._getValue());
            
            if(derivativeSetMatches != null) {
                ///TODO: We need to use multiple and account for "submission" number.
                int firstDerivativeSetMatch = derivativeSetMatches.iterator().next();
                IPLDerivativeGroupHollow derivativeSet = api.getIPLDerivativeGroupHollow(firstDerivativeSetMatch);
                Set<Artwork> artworkSet = getArtworkSet(entityId, descMap);
                
                transformArtworks(entityId, sourceFileId, ordinalPriority, seqNum, attributes, derivativeSet, localeSet, artworkSet);
            }
        }

        for (Map.Entry<Integer, Set<Artwork>> entry : descMap.entrySet()) {
            Integer id = entry.getKey();
            CharacterImages images = new CharacterImages();
            images.id = id;
            images.artworks = createArtworkByTypeMap(entry.getValue());
            mapper.addObject(images);
        }
    }
}
