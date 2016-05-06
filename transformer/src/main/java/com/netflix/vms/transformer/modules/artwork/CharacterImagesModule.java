package com.netflix.vms.transformer.modules.artwork;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeListHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.CharacterArtworkHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.CharacterImages;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharacterImagesModule extends ArtWorkModule{

    public CharacterImagesModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super("Character", api, ctx, mapper, indexer);
    }

    @Override
    public void transform() {
        Map<Integer, Set<Artwork>> descMap = new HashMap<>();
        for(CharacterArtworkHollow artworkHollowInput : api.getAllCharacterArtworkHollow()) {
            int entityId = (int) artworkHollowInput._getCharacterId();
            ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
            Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
            if(localeSet.isEmpty()) {
                ctx.getLogger().error("MissingLocaleForArtwork", String.format("Missing artwork locale for %s with id=%d; data will be dropped.", entityType, entityId));
                continue;
            }

            String sourceFileId = artworkHollowInput._getSourceFileId()._getValue();
            int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
            int seqNum = (int) artworkHollowInput._getSeqNum();
            ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();
            ArtworkDerivativeListHollow derivatives = artworkHollowInput._getDerivatives();
            Set<Artwork> artworkSet = getArtworkSet(entityId, descMap);

            transformArtworks(entityId, sourceFileId, ordinalPriority, seqNum, attributes, derivatives, localeSet, artworkSet);
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
