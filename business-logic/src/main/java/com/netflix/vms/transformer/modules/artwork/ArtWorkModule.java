package com.netflix.vms.transformer.modules.artwork;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.UnknownArtworkImageType;
import static com.netflix.vms.transformer.index.IndexSpec.ARTWORK_IMAGE_FORMAT;
import static com.netflix.vms.transformer.index.IndexSpec.ARTWORK_RECIPE;
import static com.netflix.vms.transformer.index.IndexSpec.ARTWORK_TERRITORY_COUNTRIES;

import com.netflix.vms.transformer.hollowinput.StringHollow;

import com.netflix.vms.transformer.CycleConstants;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.common.collect.ComparisonChain;
import com.netflix.hollow.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.write.objectmapper.NullablePrimitiveBoolean;
import com.netflix.vms.transformer.ConversionUtils;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.*;
import com.netflix.vms.transformer.hollowoutput.*;
import com.netflix.vms.transformer.hollowoutput.Integer;
import com.netflix.vms.transformer.index.VMSTransformerIndexer;
import com.netflix.vms.transformer.modules.AbstractTransformModule;
import com.netflix.vms.transformer.util.NFLocaleUtil;

public abstract class ArtWorkModule extends AbstractTransformModule{
    protected final String entityType;
    protected final HollowPrimaryKeyIndex imageTypeIdx;
    protected final HollowPrimaryKeyIndex recipeIdx;
    protected final HollowPrimaryKeyIndex territoryIdx;
    private final ArtWorkComparator artworkComparator;

    private final Map<String, ArtWorkImageTypeEntry> imageTypeEntryCache;
    private final Map<String, ArtWorkImageFormatEntry> imageFormatEntryCache;
    private final Map<String, ArtWorkImageRecipe> imageRecipeCache;
    private final Map<ArtworkCdn, ArtworkCdn> cdnLocationCache;
    
    private final boolean isEnableCdnDirectoryOptimization;
    private final int computedCdnFolderLen;

    private final Set<String> unknownArtworkImageTypes = new HashSet<String>();

    public ArtWorkModule(String entityType, VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper, CycleConstants cycleConstants, VMSTransformerIndexer indexer) {
        super(api, ctx, cycleConstants, mapper);
        this.entityType = entityType;
        this.imageTypeIdx = indexer.getPrimaryKeyIndex(ARTWORK_IMAGE_FORMAT);
        this.recipeIdx = indexer.getPrimaryKeyIndex(ARTWORK_RECIPE);
        this.territoryIdx = indexer.getPrimaryKeyIndex(ARTWORK_TERRITORY_COUNTRIES);
        this.artworkComparator = new ArtWorkComparator(ctx);
        this.imageFormatEntryCache = new HashMap<String, ArtWorkImageFormatEntry>();
        this.imageTypeEntryCache = new HashMap<String, ArtWorkImageTypeEntry>();
        this.imageRecipeCache = new HashMap<String, ArtWorkImageRecipe>();
        this.cdnLocationCache = new HashMap<ArtworkCdn, ArtworkCdn>();
        
        this.computedCdnFolderLen = ctx.getConfig().getComputedCdnFolderLength();
        this.isEnableCdnDirectoryOptimization = ctx.getConfig().isEnableCdnDirectoryOptimization();
    }

    protected void transformArtworks(int entityId, String sourceFileId, int ordinalPriority, int seqNum, ArtworkAttributesHollow attributes, ArtworkDerivativeSetHollow inputDerivatives, Set<ArtworkLocaleHollow> localeSet, Set<Artwork> artworkSet) {
        unknownArtworkImageTypes.clear();

        Artwork artwork = new Artwork();
        
        // Process list of derivatives
        processDerivativesAndCdnList(entityId, sourceFileId, inputDerivatives, artwork);

        artwork.sourceFileId = new Strings(sourceFileId);
        artwork.seqNum = seqNum;
        artwork.ordinalPriority = ordinalPriority;
        fillPassThroughData(artwork, attributes);

        for (final ArtworkLocaleHollow localeHollow : localeSet) {
            Artwork localeArtwork = artwork.clone();
            localeArtwork.locale = NFLocaleUtil.createNFLocale(localeHollow._getBcp47Code()._getValue());
            localeArtwork.effectiveDate = localeHollow._getEffectiveDate()._getValue();
            artworkSet.add(localeArtwork);
        }
    }

    // Process Derivatives
    protected void processDerivativesAndCdnList(int entityId, String sourceFileId, ArtworkDerivativeSetHollow inputDerivatives, Artwork artwork) {
        int inputDerivativeSetOrdinal = inputDerivatives.getOrdinal();
                
        ArtworkDerivatives outputDerivatives = cycleConstants.artworkDerivativesCache.getResult(inputDerivativeSetOrdinal);
        if(outputDerivatives != null) {
            artwork.derivatives = outputDerivatives;
            artwork.cdns = cycleConstants.cdnListCache.getResult(inputDerivativeSetOrdinal);
            return;
        }
        
        List<ArtworkCdn> cdnList = new ArrayList<>();
        List<ArtworkDerivative> derivativeList = new ArrayList<>();
                
        for (ArtworkDerivativeHollow derivativeHollow : sortInputDerivatives(inputDerivatives)) {
            int inputDerivativeOrdinal = derivativeHollow.getOrdinal();
            
            ArtworkDerivative outputDerivative = cycleConstants.artworkDerivativeCache.getResult(inputDerivativeOrdinal);
            
            if(outputDerivative == null) {
                outputDerivative = new ArtworkDerivative();
                        
                ArtWorkImageFormatEntry formatEntry = getImageFormatEntry(derivativeHollow);
                ArtWorkImageTypeEntry typeEntry = getImageTypeEntry(derivativeHollow);
                ArtWorkImageRecipe recipeEntry = getImageRecipe(derivativeHollow);
                if (typeEntry == null) {
                    String imageType = derivativeHollow._getImageType()._getValue();
                    if(!unknownArtworkImageTypes.contains(imageType)) {
                        ctx.getLogger().warn(UnknownArtworkImageType, "Unknown Image Type for entity={}, id={}, type={}; data will be dropped.", entityType, entityId, imageType);
                        unknownArtworkImageTypes.add(imageType);
                    }
                    continue;
                }
                
                String recipeDescriptor = derivativeHollow._getRecipeDescriptor()._getValue();
                
                outputDerivative.format = formatEntry;
                outputDerivative.type = typeEntry;
                outputDerivative.recipe = recipeEntry;
                outputDerivative.recipeDesc = new Strings(recipeDescriptor);
                
                outputDerivative = cycleConstants.artworkDerivativeCache.setResult(inputDerivativeOrdinal, outputDerivative);
            } 
            
            derivativeList.add(outputDerivative);
            

            ArtworkCdn cdn = new ArtworkCdn();
            cdn.cdnId = java.lang.Integer.parseInt(derivativeHollow._getCdnId()._getValue()); // @TODO: Is it Integer or String
            cdn.cdnDirectory = getCdnDirectory(sourceFileId, derivativeHollow);

            ArtworkCdn canonicalCdn = cdnLocationCache.get(cdn);
            if(canonicalCdn != null) {
                cdn = canonicalCdn;
            } else {
                cdnLocationCache.put(cdn, cdn);
            }

            cdnList.add(cdn);
        }
        
        outputDerivatives = artworkDerivatives(derivativeList);
        
        outputDerivatives = cycleConstants.artworkDerivativesCache.setResult(inputDerivativeSetOrdinal, outputDerivatives);
        cdnList = cycleConstants.cdnListCache.setResult(inputDerivativeSetOrdinal, cdnList);
        
        artwork.cdns = cdnList;
        artwork.derivatives = outputDerivatives;
    }

    protected final Strings getCdnDirectory(String sourceId, ArtworkDerivativeHollow derivative) {
        StringHollow cdnDirString = derivative._getCdnDirectory();
        if(cdnDirString == null)
        	return null;
        
		String cdnDirectory = cdnDirString._getValue();
        if (isEnableCdnDirectoryOptimization) {
            String recipeDescriptor = derivative._getRecipeDescriptor()._getValue();
            String filename_without_extension = createFilenameWithoutExtension(sourceId, recipeDescriptor);

            String derivedCdnDirectory = getCdnFolderFromFilename(filename_without_extension, computedCdnFolderLen);
            if (derivedCdnDirectory != null && derivedCdnDirectory.equals(cdnDirectory)) {
                // cdnFolder is also derivable (client already has this logic) so not need to eat-up blob space
                return null;
            }
        }
        return new Strings(cdnDirectory);
    }

    private static String getCdnFolderFromFilename(String filename, int len) {
        if (len <= 0) return "";
        if (filename == null || filename.length() <= len) return filename;
        return filename.substring(filename.length() - len);
    }

    private static String createFilenameWithoutExtension(String sourceId, String recipeDescriptor) {
        String filename_work_in_progress = sourceId + "_" + recipeDescriptor;
        String filename_without_extension = DigestUtils.shaHex(filename_work_in_progress);
        return filename_without_extension;
    }

    private ArtworkDerivatives artworkDerivatives(List<ArtworkDerivative> derivatives) {
        ArtworkDerivatives result = new ArtworkDerivatives();

        result.list = derivatives;
        result.formatToDerivativeIndex = new HashMap<>();
        result.typeFormatIndex = new HashMap<>();

        for (int i = 0; i < derivatives.size(); i++) {
            ArtworkDerivative derivative = derivatives.get(i);
            Integer index = new Integer(i);

            { // Map ImageType -> Map<Format, List<index>
                Map<ArtWorkImageFormatEntry, List<Integer>> formatMap = result.typeFormatIndex.get(derivative.type);
                if (formatMap == null) {
                    formatMap = new HashMap<>();
                    result.typeFormatIndex.put(derivative.type, formatMap);
                }

                List<Integer> idxList = formatMap.get(derivative.format);
                if (idxList == null) {
                    idxList = new ArrayList<Integer>();
                    formatMap.put(derivative.format, idxList);
                }
                idxList.add(index);
            }

            { // Legacy : just to be backwards compatible for older client < 59.50
                List<Integer> idxList = result.formatToDerivativeIndex.get(derivative.format);
                if (idxList == null) {
                    idxList = new ArrayList<Integer>();
                    result.formatToDerivativeIndex.put(derivative.format, idxList);
                }
                idxList.add(index);
            }

        }

        return result;
    }

    public Map<Strings, List<Artwork>> createArtworkByTypeMap(Collection<Artwork> allArtwork) {
        Map<Strings, List<Artwork>> artworks = new HashMap<>();

        Set<Strings> imageTypes = new HashSet<>();
        for (Artwork artwork : allArtwork) {
            imageTypes.clear();

            for (Map.Entry<ArtWorkImageTypeEntry, ?> entry : artwork.derivatives.typeFormatIndex.entrySet()) {
                Strings imageType = new Strings(entry.getKey().nameStr);
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

    public Map<ArtWorkImageTypeEntry, Set<ArtWorkImageFormatEntry>> createFormatByTypeMap(Collection<Artwork> allArtwork) {
        Map<ArtWorkImageTypeEntry, Set<ArtWorkImageFormatEntry>> map = new HashMap<>();

        for (Artwork artwork : allArtwork) {
            for (Map.Entry<ArtWorkImageTypeEntry, Map<ArtWorkImageFormatEntry, List<Integer>>> entry : artwork.derivatives.typeFormatIndex.entrySet()) {
                ArtWorkImageTypeEntry imageType = entry.getKey();

                Set<ArtWorkImageFormatEntry> set = map.get(imageType);
                if (set == null) {
                    set = new HashSet<ArtWorkImageFormatEntry>();
                    map.put(imageType, set);
                }
                set.addAll(entry.getValue().keySet());
            }
        }
        return map;
    }

    protected void fillPassThroughData(Artwork desc, ArtworkAttributesHollow attributes) {
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
        PassthroughString passThroughString = getPassThroughString("APPROVAL_SOURCE", keyValues);
        boolean setBasicPassThrough = false;
        if(passThroughString != null) {
            passThrough.approval_source = passThroughString;
            setBasicPassThrough = true;
        }
        String approvalState = keyValues.get("APPROVAL_STATE");
        if(approvalState != null) {
            // NOTE: Need to manually make approval_state to NullablePrimitiveBoolean (public NullablePrimitiveBoolean approval_state = null)
            passThrough.approval_state = java.lang.Boolean.valueOf(approvalState) ? NullablePrimitiveBoolean.TRUE : NullablePrimitiveBoolean.FALSE;
            setBasicPassThrough = true;
        }
        passThroughString = getPassThroughString("designAttribute", keyValues);
        if(passThroughString != null) {
            passThrough.design_attribute = passThroughString;
            setBasicPassThrough = true;
        }
        passThroughString = getPassThroughString("FOCAL_POINT", keyValues);
        if(passThroughString != null) {
            passThrough.focal_point = passThroughString;
            setBasicPassThrough = true;
        }            // Sort descriptor necessary for client artwork resolver

        passThroughString = getPassThroughString("TONE", keyValues);
        if(passThroughString != null) {
            passThrough.tone = passThroughString;
            setBasicPassThrough = true;
        }
        passThroughString = getPassThroughString("GROUP_ID", keyValues);
        if(passThroughString != null) {
            passThrough.group_id = passThroughString;
            setBasicPassThrough = true;
        }
        if (keyListValues.containsKey("AWARD_CAMPAIGNS")) {
            passThrough.awardCampaigns = keyListValues.get("AWARD_CAMPAIGNS");
            setBasicPassThrough = true;
        }
        if (keyListValues.containsKey("themes")) {
            passThrough.themes = keyListValues.get("themes");
            setBasicPassThrough = true;
        }
        if (keyListValues.containsKey("IDENTIFIERS")) {
            passThrough.identifiers = keyListValues.get("IDENTIFIERS");
            setBasicPassThrough = true;
        }
        if (keyListValues.containsKey("PERSON_IDS")) {
            passThrough.personIdStrs = keyListValues.get("PERSON_IDS");
            setBasicPassThrough = true;
        }

        ArtworkSourcePassthrough sourcePassThrough = new ArtworkSourcePassthrough();
        sourcePassThrough.source_file_id = getArtworkSourceString("source_file_id", keyValues);
        sourcePassThrough.original_source_file_id = getArtworkSourceString("original_source_file_id", keyValues);
        if (sourcePassThrough.original_source_file_id == null) sourcePassThrough.original_source_file_id = sourcePassThrough.source_file_id;

        if(setBasicPassThrough) {
            desc.basic_passthrough = passThrough;
        }
        desc.source = sourcePassThrough;
        desc.file_seq = java.lang.Integer.valueOf(keyValues.get("file_seq"));
        desc.source_movie_id = getPassThroughVideo("SOURCE_MOVIE_ID", keyValues);
        desc.acquisitionSource = getAcquisitionSource("ACQUISITION_SOURCE", keyValues);
    }

    private PassthroughVideo getPassThroughVideo(String key, HashMap<String, String> keyValues) {
        PassthroughString passThroughString = getPassThroughString(key, keyValues);
        if (passThroughString == null) return null;

        String videoStr = new String(passThroughString.value);
        return new PassthroughVideo(java.lang.Integer.parseInt(videoStr));
    }

    private PassthroughString getPassThroughString(String key, HashMap<String, String> keyValues) {
        String value = keyValues.get(key);
        if(value != null) {
            return new PassthroughString(value);
        }
        return null;
    }

    private AcquisitionSource getAcquisitionSource(String key, HashMap<String, String> keyValues) {
        String value = keyValues.get(key);
        if (value != null) {
            return new AcquisitionSource(value);
        }
        return null;
    }

    private ArtworkSourceString getArtworkSourceString(String key, HashMap<String, String> keyValues) {
        String value = keyValues.get(key);
        if(value != null) {
            return new ArtworkSourceString(value);
        }
        return null;
    }

    protected final ArtWorkImageTypeEntry getImageTypeEntry(ArtworkDerivativeHollow derivative) {
        StringHollow imageTypeHollow = derivative._getImageType();
        return getImageTypeEntry(imageTypeHollow._getValue());
    }

    protected final ArtWorkImageTypeEntry getImageTypeEntry(String typeName) {
        ArtWorkImageTypeEntry entry = imageTypeEntryCache.get(typeName);

        if(entry == null) {
            int ordinal = imageTypeIdx.getMatchingOrdinal(typeName);
            entry = new ArtWorkImageTypeEntry();
            if(ordinal != -1) {
                ArtWorkImageTypeHollow artWorkImageTypeHollow = api.getArtWorkImageTypeHollow(ordinal);
                entry.recipeNameStr = artWorkImageTypeHollow._getRecipe()._getValue().toCharArray();
                entry.allowMultiples = true;
                entry.unavailableFileNameStr = "unavailable".toCharArray();
                entry.nameStr = typeName.toCharArray();
            }else {
                // RETURN NULL to be backwards compatible
                return null;
                //                entry.recipeNameStr = "jpg".toCharArray();
                //                entry.allowMultiples = true;
                //                entry.unavailableFileNameStr = "unavailable".toCharArray();
                //                entry.nameStr = typeName.toCharArray();
            }

            imageTypeEntryCache.put(typeName, entry);
        }

        return entry;
    }

    protected final ArtWorkImageFormatEntry getImageFormatEntry(ArtworkDerivativeHollow derivative) {
        int width = (int)derivative._getWidth();
        int height = (int)derivative._getHeight();
        String formatName = width + "x" + height;

        ArtWorkImageFormatEntry entry = imageFormatEntryCache.get(formatName);

        if(entry == null) {
            entry = new ArtWorkImageFormatEntry();
            entry.nameStr = formatName.toCharArray();
            entry.height = height;
            entry.width = width;

            imageFormatEntryCache.put(formatName, entry);
        }

        return entry;
    }

    protected final ArtWorkImageRecipe getImageRecipe(ArtworkDerivativeHollow derivative) {
        String recipeName = derivative._getRecipeName()._getValue();

        ArtWorkImageRecipe entry = imageRecipeCache.get(recipeName);

        if(entry == null) {
            int ordinal = recipeIdx.getMatchingOrdinal(recipeName);
            entry = new ArtWorkImageRecipe();
            if(ordinal != -1) {
                ArtworkRecipeHollow artworkRecipeHollow = api.getArtworkRecipeHollow(ordinal);
                entry.cdnFolderStr = ConversionUtils.getCharArray(artworkRecipeHollow._getCdnFolder());
                entry.extensionStr = ConversionUtils.getCharArray(artworkRecipeHollow._getExtension());
                entry.recipeNameStr = ConversionUtils.getCharArray(artworkRecipeHollow._getRecipeName());
                StringHollow hostName = artworkRecipeHollow._getHostName();
                if(hostName != null)
                    entry.hostNameStr = ConversionUtils.getCharArray(hostName);
            }else {
                entry.cdnFolderStr = ConversionUtils.getCharArray(derivative._getCdnDirectory());
                entry.extensionStr = recipeName.toCharArray();
                entry.recipeNameStr = recipeName.toCharArray();
            }

            imageRecipeCache.put(recipeName, entry);
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

    protected Set<Artwork> getArtworkSet(int entityId, Map<java.lang.Integer, Set<Artwork>> artMap) {
        Set<Artwork> artworkSet = artMap.get(entityId);
        if (artworkSet == null) {
            artworkSet = new LinkedHashSet<>();
            artMap.put(entityId, artworkSet);
        }
        return artworkSet;
    }

    protected List<ArtworkDerivativeHollow> sortInputDerivatives(ArtworkDerivativeSetHollow derivatives) {
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
