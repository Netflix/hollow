package com.netflix.vms.transformer.modules.artwork;

import static com.netflix.vms.transformer.index.IndexSpec.ARTWORK_RECIPE;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ComparisonChain;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.ConversionUtils;
import com.netflix.vms.transformer.TransformerContext;
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
import com.netflix.vms.transformer.hollowinput.SingleValuePassthroughMapHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageFormatEntry;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageRecipe;
import com.netflix.vms.transformer.hollowoutput.ArtWorkImageTypeEntry;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import com.netflix.vms.transformer.hollowoutput.ArtworkBasicPassthrough;
import com.netflix.vms.transformer.hollowoutput.ArtworkCdn;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivative;
import com.netflix.vms.transformer.hollowoutput.ArtworkDerivatives;
import com.netflix.vms.transformer.hollowoutput.ArtworkSourcePassthrough;
import com.netflix.vms.transformer.hollowoutput.ArtworkSourceString;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.hollowoutput.NFLocale;
import com.netflix.vms.transformer.hollowoutput.PassthroughString;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.__passthrough_string;
import com.netflix.vms.transformer.index.IndexSpec;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class ArtWorkModule extends AbstractTransformModule{
    protected final String entityType;
    private final HollowPrimaryKeyIndex imageTypeIdx;
    private final HollowPrimaryKeyIndex recipeIdx;
    private final ArtWorkComparator artworkComparator;

    public ArtWorkModule(String entityType, VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, VMSTransformerIndexer indexer) {
        super(api, ctx, mapper);
        this.entityType = entityType;
        this.imageTypeIdx = indexer.getPrimaryKeyIndex(IndexSpec.ARTWORK_IMAGE_FORMAT);
        this.recipeIdx = indexer.getPrimaryKeyIndex(ARTWORK_RECIPE);
        this.artworkComparator = new ArtWorkComparator(ctx);
    }

    protected void transformArtworks(int entityId, String sourceFileId, int ordinalPriority, int seqNum, ArtworkAttributesHollow attributes, ArtworkDerivativeListHollow derivatives, Set<ArtworkLocaleHollow> localeSet, Set<Artwork> artworkSet) {

        // Process list of derivatives
        List<ArtworkDerivative> derivativeList = new ArrayList<ArtworkDerivative>();
        List<ArtworkCdn> cdnList = new ArrayList<ArtworkCdn>();
        for (ArtworkDerivativeHollow derivativeHollow : sortInputDerivatives(derivatives)) {
            ArtWorkImageFormatEntry formatEntry = getImageFormatEntry(derivativeHollow);
            ArtWorkImageTypeEntry typeEntry = getImageTypeEntry(derivativeHollow);
            ArtWorkImageRecipe recipeEntry = getImageRecipe(derivativeHollow);
            if (typeEntry == null) {
                ctx.getLogger().error("UnknownArtworkImageType", String.format("Unknown Image Type for entity=%s, id=%s, type=%s; data will be dropped.", entityType, entityId, derivativeHollow._getImageType()._getValue()));
                continue;
            }

            ArtworkDerivative derivative = new ArtworkDerivative();
            derivative.format = formatEntry;
            derivative.type = typeEntry;
            derivative.recipe = recipeEntry;
            derivative.recipeDesc = new Strings(derivativeHollow._getRecipeDescriptor()._getValue());
            derivativeList.add(derivative);

            ArtworkCdn cdn = new ArtworkCdn();
            cdn.cdnId = java.lang.Integer.parseInt(derivativeHollow._getCdnId()._getValue()); // @TODO: Is it Integer or String
            cdn.cdnDirectory = new Strings(derivativeHollow._getCdnDirectory()._getValue());
            cdnList.add(cdn);
        }

        for (final ArtworkLocaleHollow localeHollow : localeSet) {
            final NFLocale locale = new NFLocale(localeHollow._getBcp47Code()._getValue());

            Artwork artwork = new Artwork();
            artwork.sourceFileId = new Strings(sourceFileId);
            artwork.seqNum = seqNum;
            artwork.ordinalPriority = ordinalPriority;
            artwork.locale = locale;
            artwork.effectiveDate = localeHollow._getEffectiveDate()._getValue();
            artwork.derivatives = artworkDerivatives(derivativeList);
            artwork.cdns = cdnList;
            fillPassThroughData(artwork, attributes);

            artworkSet.add(artwork);
        }
    }

    private ArtworkDerivatives artworkDerivatives(List<ArtworkDerivative> derivatives) {
        ArtworkDerivatives result = new ArtworkDerivatives();

        result.list = derivatives;
        result.formatToDerivativeIndex = new HashMap<>();

        for (int i = 0; i < derivatives.size(); i++) {
            ArtworkDerivative derivative = derivatives.get(i);

            List<Integer> list = result.formatToDerivativeIndex.get(derivative.format);
            if (list == null) {
                list = new ArrayList<Integer>();
                result.formatToDerivativeIndex.put(derivative.format, list);
            }

            list.add(new Integer(i));
        }

        return result;
    }

    public Map<Strings, List<Artwork>> createArtworkByTypeMap(Collection<Artwork> allArtwork) {
        Map<Strings, List<Artwork>> artworks = new HashMap<>();

        Set<Strings> imageTypes = new HashSet<>();
        for (Artwork artwork : allArtwork) {
            imageTypes.clear();
            for (ArtworkDerivative derivative : artwork.derivatives.list) {
                imageTypes.add(new Strings(derivative.type.nameStr.toString()));
            }

            for (Strings imageType : imageTypes) {
                List<Artwork> list = artworks.get(imageType);
                if (list == null) {
                    list = new ArrayList<Artwork>();
                    artworks.put(imageType, list);
                }
                list.add(artwork);
            }
        }

        for (Map.Entry<Strings, List<Artwork>> entry : artworks.entrySet()) {
            Collections.sort(entry.getValue(), artworkComparator);
        }

        return artworks;
    }

    private void fillPassThroughData(Artwork desc, ArtworkAttributesHollow attributes) {
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

    protected Set<Artwork> getArtworkSet(Integer personId, Map<Integer, Set<Artwork>> descMap) {
        Set<Artwork> artworkSet = descMap.get(personId);
        if (artworkSet == null) {
            artworkSet = new HashSet<>();
            descMap.put(personId, artworkSet);
        }
        return artworkSet;
    }

    protected List<ArtworkDerivativeHollow> sortInputDerivatives(ArtworkDerivativeListHollow derivatives) {
        List<ArtworkDerivativeHollow> sortedDerivativeHollowList = new ArrayList<>();
        for (ArtworkDerivativeHollow derivativeHollow : derivatives) {
            sortedDerivativeHollowList.add(derivativeHollow);
        }
        Collections.sort(sortedDerivativeHollowList, new Comparator<ArtworkDerivativeHollow>() {
            @Override
            public int compare(ArtworkDerivativeHollow o1, ArtworkDerivativeHollow o2) {
                return ComparisonChain.start()
                        .compare(o1._getImageType()._getValue(), o2._getImageType()._getValue())
                        .compare(o1._getWidth(), o2._getWidth())
                        .compare(o1._getHeight(), o2._getHeight())
                        .compare(o1._getRecipeName()._getValue(), o2._getRecipeName()._getValue())
                        .compare(o1._getRecipeDescriptor()._getValue(), o2._getRecipeDescriptor()._getValue())
                        .result();
            }
        });
        return sortedDerivativeHollowList;
    }

}
