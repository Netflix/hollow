package com.netflix.vms.transformer.modules.artwork;

import static com.netflix.vms.transformer.index.IndexSpec.ARTWORK_RECIPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.ConversionUtils;
import com.netflix.vms.transformer.hollowinput.ArtWorkImageTypeHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkAttributesHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkDerivativeListHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkLocaleListHollow;
import com.netflix.vms.transformer.hollowinput.ArtworkRecipeHollow;
import com.netflix.vms.transformer.hollowinput.ListOfStringHollow;
import com.netflix.vms.transformer.hollowinput.MapKeyHollow;
import com.netflix.vms.transformer.hollowinput.MultiValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.PersonArtworkHollow;
import com.netflix.vms.transformer.hollowinput.SingleValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkDescriptor;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;
import com.netflix.vms.transformer.hollowoutput.ArtworkBasicPassthrough;
import com.netflix.vms.transformer.hollowoutput.ArtworkSourcePassthrough;
import com.netflix.vms.transformer.hollowoutput.ArtworkSourceString;
import com.netflix.vms.transformer.hollowoutput.AssetLocation;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.PassthroughString;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.__passthrough_string;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

public abstract class ArtWorkModule extends AbstractTransformModule{
    private final HollowPrimaryKeyIndex imageTypeIdx;
    private final HollowPrimaryKeyIndex recipeIdx;
    
    public ArtWorkModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
	    super(api, mapper);
	    this.imageTypeIdx = indexer.getPrimaryKeyIndex(IndexSpec.ARTWORK_IMAGE_FORMAT);
	    this.recipeIdx = indexer.getPrimaryKeyIndex(ARTWORK_RECIPE);
	}
	
    protected void transformArtWorkDescriptors(Set<ArtworkLocaleHollow> localeSet,
            Set<ArtWorkDescriptor> descriptorSet, Map<Long, DescriptorGroup> descGroupMap, int ordinalPriority,
            int seqNum, ArtworkAttributesHollow attributes, ArtworkDerivativeListHollow derivatives) {
        for (final ArtworkLocaleHollow localeHollow : localeSet) {
            final NFLocale locale = new NFLocale(localeHollow._getBcp47Code()._getValue());
            for (ArtworkDerivativeHollow derivative : derivatives) {
                Long imageId = derivative._getImageId();
                ArtWorkImageFormatEntry formatEntry = getImageFormatEntry(derivative);
                ArtWorkImageTypeEntry typeEntry = getImageTypeEntry(derivative);
                Set<ArtWorkImageRecipe> recipes = descGroupMap.get(imageId).recipes;
                Map<Strings, AssetLocation> assetLocations = descGroupMap.get(imageId).assetLocations;
                ArtWorkDescriptor desc = new ArtWorkDescriptor();
                desc.locale = locale;
                desc.format = formatEntry;
                desc.imageType = typeEntry;
                desc.imageId = imageId;
                desc.isUsDescriptor = true;
                desc.seqNum = seqNum;
                desc.recipes = recipes;
                desc.ordinalPriority = ordinalPriority;
                desc.effectiveDate = localeHollow._getEffectiveDate()._getValue();
                desc.assetLocationMap = assetLocations;
                fillPassThroughData(desc, attributes);
                descriptorSet.add(desc);
            }
        }
    }
    
    private void fillPassThroughData(ArtWorkDescriptor desc, ArtworkAttributesHollow attributes) {
        SingleValuePassthroughMapHollow singleValuePassThrough = attributes._getPassthrough()._getSingleValues();
        HashMap<String, String> keyValues = new HashMap<>();
        for(Entry<MapKeyHollow, StringHollow> entry : singleValuePassThrough.entrySet()) {
            keyValues.put(entry.getKey()._getValue(), entry.getValue()._getValue());
        }
        
        HashMap<String, List<__passthrough_string>> keyListValues = new HashMap<>();
        MultiValuePassthroughMapHollow multiValuePassthrough = attributes._getPassthrough()._getMultiValues();
        for(Entry<MapKeyHollow, ListOfStringHollow> entry : multiValuePassthrough.entrySet()) {
            String key = entry.getKey()._getValue();
            List<__passthrough_string> values = new ArrayList<>();
            ListOfStringHollow listValue = entry.getValue();
            Iterator<StringHollow> iterator = listValue.iterator();
            while(iterator.hasNext()) {
                StringHollow next = iterator.next();
                values.add(new __passthrough_string(next._getValue()));
            }
            keyListValues.put(key, values);
        }
        
        ArtworkBasicPassthrough passThrough = new ArtworkBasicPassthrough();
        PassthroughString passThroughString = getPassThroughString("approval_source", keyValues);
        boolean setBasicPassThrough = false;
        if(passThroughString != null) {
            passThrough.approval_source = passThroughString;
            setBasicPassThrough = true;
        }
        String approvalState = keyValues.get("APPROVAL_STATE");
        if(approvalState != null) {
            passThrough.approval_state = java.lang.Boolean.valueOf(approvalState);
            setBasicPassThrough = true;
        }
        passThroughString = getPassThroughString("design_attribute", keyValues);
        if(passThroughString != null) {
            passThrough.design_attribute = passThroughString;
            setBasicPassThrough = true;
        }
        passThroughString = getPassThroughString("focal_point", keyValues);
        if(passThroughString != null) {
            passThrough.focal_point = passThroughString;
            setBasicPassThrough = true;
        }            // Sort descriptor necessary for client artwork resolver

        passThroughString = getPassThroughString("tone", keyValues);
        if(passThroughString != null) {
            passThrough.tone = passThroughString;
            setBasicPassThrough = true;
        }
        passThroughString = getPassThroughString("GROUP_ID", keyValues);
        if(passThroughString != null) {
            passThrough.group_id = passThroughString;
            setBasicPassThrough = true;
        }
        
        if(passThroughString != null) {
            passThrough.awardCampaigns = keyListValues.get("awardCampaigns");
            setBasicPassThrough = true;
        }
        if(passThroughString != null) {
            passThrough.themes = keyListValues.get("themes");
            setBasicPassThrough = true;
        }
        
        ArtworkSourcePassthrough sourcePassThrough = new ArtworkSourcePassthrough();
        sourcePassThrough.original_source_file_id = getArtworkSourceString("source_file_id", keyValues);
        sourcePassThrough.source_file_id = getArtworkSourceString("source_file_id", keyValues);
        
        if(setBasicPassThrough) {
            desc.basic_passthrough = passThrough;
        }
        desc.source = sourcePassThrough;
        desc.file_seq = java.lang.Integer.valueOf(keyValues.get("file_seq"));
    }

    private PassthroughString getPassThroughString(String key, HashMap<String, String> keyValues) {
        PassthroughString passthroughString = new PassthroughString();
        String value = keyValues.get(key);
        if(value != null) {
            passthroughString.value = value.toCharArray();
            return passthroughString;
        }
        return null;            
    }
    
    private ArtworkSourceString getArtworkSourceString(String key, HashMap<String, String> keyValues) {
        ArtworkSourceString passthroughString = new ArtworkSourceString();
        String value = keyValues.get(key);
        if(value != null) {
            passthroughString.value = value.toCharArray();
            return passthroughString;
        }
        return null;
    }

    protected static class DescriptorGroup {
        Set<ArtWorkImageRecipe> recipes = new HashSet<>();
        Map<Strings, AssetLocation> assetLocations = new HashMap<>();
    }

    protected final Map<Long, DescriptorGroup> groupData(final ArtworkDerivativeListHollow derivatives) {
        Map<Long, DescriptorGroup> descGroupMap = new HashMap<>();
        for (ArtworkDerivativeHollow derivative : derivatives) {
            Long imageId = derivative._getImageId();
            DescriptorGroup group = descGroupMap.get(imageId);
            if (group == null) {
                group = new DescriptorGroup();
                descGroupMap.put(imageId, group);
            }
            ArtWorkImageRecipe recipe = getImageRecipe(derivative);
            group.recipes.add(recipe);
            if(isAssetLocationEnabled()) {
                AssetLocation assetLocation = new AssetLocation();
                assetLocation.cdnDirectory = ConversionUtils.getStrings(derivative._getCdnDirectory());
                assetLocation.cdnId = ConversionUtils.getInt(derivative._getCdnId());
                assetLocation.recipeDescriptor = ConversionUtils.getStrings(derivative._getRecipeDescriptor());
                group.assetLocations.put(new Strings(recipe.recipeNameStr), assetLocation);
            }
        }
        return descGroupMap;
    }
    
    protected boolean isAssetLocationEnabled() {
        return false;
    }
    
    protected final ArtWorkImageTypeEntry getImageTypeEntry(ArtworkDerivativeHollow derivative) {
        StringHollow imageTypeHollow = derivative._getImageType();
        int ordinal = imageTypeIdx.getMatchingOrdinal(imageTypeHollow._getValue());
        ArtWorkImageTypeEntry entry = new ArtWorkImageTypeEntry();
        if(ordinal != -1) {
            ArtWorkImageTypeHollow artWorkImageTypeHollow = api.getArtWorkImageTypeHollow(ordinal);
            entry.recipeNameStr = artWorkImageTypeHollow._getRecipe()._getValue().toCharArray();
            entry.allowMultiples = true;
            entry.unavailableFileNameStr = "unavailable".toCharArray();
            entry.nameStr = imageTypeHollow._getValue().toCharArray();
        }else {
            entry.recipeNameStr = "jpg".toCharArray();
            entry.allowMultiples = true;
            entry.unavailableFileNameStr = "unavailable".toCharArray();
            entry.nameStr = imageTypeHollow._getValue().toCharArray();
        }
        
        return entry;
    }

    protected final ArtWorkImageFormatEntry getImageFormatEntry(ArtworkDerivativeHollow derivative) {
        int width = (int)derivative._getWidth();
        int height = (int)derivative._getHeight();
        String formatName = width + "x" + height;
        ArtWorkImageFormatEntry entry = new ArtWorkImageFormatEntry();
        entry.nameStr = formatName.toCharArray();
        entry.height = height;
        entry.width = width;
        return entry;
    }

    protected final ArtWorkImageRecipe getImageRecipe(ArtworkDerivativeHollow derivative) {
        StringHollow recipeNameHollow = derivative._getRecipeName();
        int ordinal = recipeIdx.getMatchingOrdinal(recipeNameHollow._getValue());
        ArtWorkImageRecipe entry = new ArtWorkImageRecipe();
        if(ordinal != -1) {
            ArtworkRecipeHollow artworkRecipeHollow = api.getArtworkRecipeHollow(ordinal);
            entry.cdnFolderStr = ConversionUtils.getCharArray(artworkRecipeHollow._getCdnFolder());
            entry.extensionStr = ConversionUtils.getCharArray(artworkRecipeHollow._getExtension());
            entry.recipeNameStr = ConversionUtils.getCharArray(artworkRecipeHollow._getRecipeName());
        }else {
            entry.cdnFolderStr = ConversionUtils.getCharArray(derivative._getCdnDirectory());
            entry.extensionStr = ConversionUtils.getCharArray(recipeNameHollow);
            entry.recipeNameStr = ConversionUtils.getCharArray(recipeNameHollow);
        }
        return entry;
    }
    
    protected Set<ArtworkLocaleHollow> getLocalTerritories(ArtworkLocaleListHollow locales) {
        Set<ArtworkLocaleHollow> artworkLocales = new HashSet<>();
        Iterator<ArtworkLocaleHollow> iterator = locales.iterator();
        while(iterator.hasNext()) {
            ArtworkLocaleHollow locale = iterator.next();
            if(locale != null) {
                artworkLocales.add(locale);
            }
        }
        return artworkLocales;
    }
    
    protected Set<ArtWorkDescriptor> getDescriptorSet(Integer personId, Map<Integer, Set<ArtWorkDescriptor>> descMap) {
        Set<ArtWorkDescriptor> descriptorSet = descMap.get(personId);
        if (descriptorSet == null) {
            descriptorSet = new HashSet<>();
            descMap.put(personId, descriptorSet);
        }
        return descriptorSet;
    }    
    
}
