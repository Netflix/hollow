package com.netflix.vms.transformer.modules.artwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeListHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.PersonArtworkHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkDescriptor;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.PersonImages;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;

public class PersonImagesModule extends ArtWorkModule{

    public PersonImagesModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
	    super(api, mapper, indexer);
	}

    @Override
    public void transform() {
        Map<Integer, Set<ArtWorkDescriptor>> descMap = new HashMap<>();
        for(PersonArtworkHollow artworkHollowInput : api.getAllPersonArtworkHollow()) {
            ArtworkLocaleListHollow locales = artworkHollowInput._getLocales();
            int personId = (int) artworkHollowInput._getPersonId();
            Set<ArtworkLocaleHollow> localeSet = getLocalTerritories(locales);
            if(localeSet.isEmpty()) {
//              ERRCODELOGGER.logf(ErrorCode.MissingLocaleForArtwork, 
//                            "Missing artwork locale for %s with id %d; data will be dropped.", "", personId);
                continue;
            }
            
            Set<ArtWorkDescriptor> descriptorSet = getDescriptorSet(new Integer(personId), descMap);

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
            // Sort descriptor necessary for client artwork resolver
            Collections.sort(descriptorList, new ArtWorkComparator());
            
            PersonImages images = new PersonImages();
            images.artWorkDescriptors = descriptorList;
            images.id = id.val;
            mapper.addObject(images);
        }
    }
}
