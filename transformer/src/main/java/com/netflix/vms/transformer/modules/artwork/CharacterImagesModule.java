package com.netflix.vms.transformer.modules.artwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeListHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.CharacterArtworkHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkDescriptor;
import com.netflix.vms.transformer.hollowoutput.CharacterImages;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

public class CharacterImagesModule extends ArtWorkModule{

    public CharacterImagesModule(VMSHollowVideoInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
	    super(api, ctx, mapper, indexer);
	}

    @Override
    public void transform() {
        Map<Integer, Set<ArtWorkDescriptor>> descMap = new HashMap<>();
        for(CharacterArtworkHollow artworkHollowInput : api.getAllCharacterArtworkHollow()) {
            ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
            int characterId = (int) artworkHollowInput._getCharacterId();
            Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
            if(localeSet.isEmpty()) {
//              ERRCODELOGGER.logf(ErrorCode.MissingLocaleForArtwork, 
//                            "Missing artwork locale for %s with id %d; data will be dropped.", "", personId);
                continue;
            }
            
            Set<ArtWorkDescriptor> descriptorSet = getDescriptorSet(new Integer(characterId), descMap);

            // Group Recipes and Filenames to be backwards compatible
            Map<Long, DescriptorGroup> descGroupMap = groupData(artworkHollowInput._getDerivatives());            
            
            int ordinalPriority = (int) artworkHollowInput._getOrdinalPriority();
            int seqNum = (int) artworkHollowInput._getSeqNum();
            ArtworkAttributesHollow attributes = artworkHollowInput._getAttributes();
            ArtworkDerivativeListHollow derivatives = artworkHollowInput._getDerivatives();
            
            transformArtWorkDescriptors(localeSet, descriptorSet, descGroupMap,
                    ordinalPriority, seqNum, attributes, derivatives);            
        }
        
        for (Map.Entry<Integer, Set<ArtWorkDescriptor>> entry : descMap.entrySet()) {
            Integer id = entry.getKey();
            List<ArtWorkDescriptor> descriptorList = new ArrayList<>(entry.getValue());
            CharacterImages images = new CharacterImages();
            images.artWorkDescriptorList = descriptorList;
            images.id = id.val;
            // Sort descriptor necessary for client artwork resolver
            Collections.sort(descriptorList, new ArtWorkComparator(ctx));
            mapper.addObject(images);
        }
    }
}
