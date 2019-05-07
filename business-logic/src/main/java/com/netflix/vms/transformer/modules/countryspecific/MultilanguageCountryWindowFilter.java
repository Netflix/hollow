package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.contract.ContractAsset;
import com.netflix.vms.transformer.contract.ContractAssetType;
import com.netflix.vms.transformer.hollowinput.RightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import com.netflix.vms.transformer.hollowoutput.EncodeSummaryDescriptor;
import com.netflix.vms.transformer.hollowoutput.EncodeSummaryDescriptorData;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TimedTextTypeDescriptor;
import com.netflix.vms.transformer.util.InputOrdinalResultCache;
import java.util.Arrays;
import java.util.List;

public class MultilanguageCountryWindowFilter {

    private final VMSHollowInputAPI api;
    
    private final InputOrdinalResultCache<ContractAsset> rightsContractAssetCache;
    private final InputOrdinalResultCache<ContractAsset> gk2RightsContractAssetCache;
    
    private final MultilanguageCountryDialectOrdinalAssigner dialectOrdinalAssigner;


    public MultilanguageCountryWindowFilter(VMSHollowInputAPI api, CycleConstants cycleConstants) {
        this.api = api;
        this.rightsContractAssetCache = cycleConstants.rightsContractAssetCache;
        this.gk2RightsContractAssetCache = cycleConstants.gk2RightsContractAssetCache;
        this.dialectOrdinalAssigner = cycleConstants.dialectOrdinalAssigner;
    }

    /**
     * Returns an integer describing the asset type rights available for the specified language.
     * AUDIO - 1 (001), SUBTITLES - 2(010), DESCRIPTIVE_AUDIO - 4 (100), AUDIO + SUBTITLES - 3 (011),
     * AUDIO + DESCRIPTIVE_AUDIO - (101), AUDIO + SUBTITLES + DESCRIPTIVE_AUDIO - (111) and so on..
     *
     * @param language
     * @param contractAssets
     * @return
     */
    public long contractAvailabilityForLanguage(String language, List<RightsContractAssetHollow> contractAssets) {
        long availability = 0;

        for (RightsContractAssetHollow assetInput : contractAssets) {
            ContractAsset asset = cachedAsset(assetInput);

            if (language.equals(asset.getLanguage())) {
                // effectively always 0, above check ensures lengths are same so method languageDialectOffset will return 0.
                int localeBitOffset = ContractAssetType.values().length * languageDialectOffset(language, asset.getLocale()); //-> not needed.

                availability |= asset.getType().getBitIdentifier() << localeBitOffset;
            }
        }

        return availability;
    }

    private ContractAsset cachedAsset(RightsContractAssetHollow assetInput) {
        InputOrdinalResultCache<ContractAsset> cache;
        if(assetInput.getTypeDataAccess().getDataAccess() == api.getDataAccess())
            cache = rightsContractAssetCache;
        else
            cache = gk2RightsContractAssetCache;
        
        ContractAsset asset = cache.getResult(assetInput.getOrdinal());
        if(asset == null) {
            asset = new ContractAsset(assetInput);
            asset = cache.setResult(assetInput.getOrdinal(), asset);
        }
        return asset;
    }
    
    /**
     * Determines if the language is available for the given package.
     * 001 - AUDIO assets only
     * 010 - SUBTITLES only
     * 011 - AUDIO and SUBTITLES
     *
     * @param language
     * @param pkg
     * @param assetTypeRightsAvailability The asset type rights as returned from contractAvailabilityForLangauge()
     * @return
     */
    public long packageIsAvailableForLanguage(String language, PackageData pkg, long assetTypeRightsAvailability) {
        if (pkg == null) return 0;
        long packageAvailability = 0;
        boolean anyLanguageDiscovered = false;

        // find AUDIO AND SUBTITLES availability in list of [TEXT or VIDEO] stream profile types.
        for (EncodeSummaryDescriptor descriptor : pkg.muxAudioStreamSummary) {

            packageAvailability |= languageIsAvailable(language, descriptor, assetTypeRightsAvailability, true, true);

            // if both assets are found in any of the streams then return.
            if (packageAvailability == (ContractAssetType.AUDIO.getBitIdentifier() | ContractAssetType.SUBTITLES.getBitIdentifier())) {
                return packageAvailability;
            }

            if (!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, true, true))
                anyLanguageDiscovered = true;
        }

        // if AUDIO asset is not found
        if ((packageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) == 0) {
            // check list of [AUDIO or MUXED] stream profile types.
            for (EncodeSummaryDescriptor descriptor : pkg.audioStreamSummary) {

                // check language availability only for audio.
                packageAvailability |= languageIsAvailable(language, descriptor, assetTypeRightsAvailability, true, false);
                if ((packageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) != 0)
                    break;

                if (!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, true, false))
                    anyLanguageDiscovered = true;
            }
        }

        // if SUBTITLES asset is not found
        if ((packageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) == 0) {
            // check list of [TEXT or VIDEO] stream profiles
            for (EncodeSummaryDescriptor descriptor : pkg.textStreamSummary) {

                // check language availability only for text
                packageAvailability |= languageIsAvailable(language, descriptor, assetTypeRightsAvailability, false, true);
                if ((packageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) != 0)
                    break;

                if (!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, false, true))
                    anyLanguageDiscovered = true;
            }
        }

        if (packageAvailability == 0 && !anyLanguageDiscovered)
            return Long.MIN_VALUE;
        return packageAvailability;
    }

    private static final char[] FORCED_CHARS = "Forced".toCharArray();

    /**
     *
     * This method returns a number.
     *
     * For language that equals audioLanguage (descriptor). It returns a number that represents
     *
     * @param language
     * @param descriptor
     * @param contractAvailability AUDIO - 1 (001), SUBTITLES - 2(010), DESCRIPTIVE_AUDIO - 4 (100), AUDIO + SUBTITLES - 3 (011),
     *                             AUDIO + DESCRIPTIVE_AUDIO - (101), AUDIO + SUBTITLES + DESCRIPTIVE_AUDIO - (111) and so on.. see method contractAvailability
     *                             It is basically a number describing assets available for the language parameter in a contract.
     * @param checkAudio
     * @param checkText
     * @return
     */
    private long languageIsAvailable(String language, EncodeSummaryDescriptor descriptor, long contractAvailability, boolean checkAudio, boolean checkText) {
        EncodeSummaryDescriptorData descriptorData = descriptor.descriptorData;

        long languageAvailability = 0;

        if (checkAudio && languageMatches(language, descriptorData.audioLanguage)) {

            // localeBitOffset will be non-zero if language is a prefix of descriptor.audioLanguage
            int localeBitOffset = ContractAssetType.values().length * languageDialectOffset(language, descriptorData.audioLanguage);

            // what is this condition?
            if (descriptorData.assetType.id == 2) {
                // since contract availability is always first 3 bits, result of this condition will always be false for localeBitOffset > 0 (<< has higher precedence than &)
                if ((contractAvailability & ContractAssetType.DESCRIPTIVE_AUDIO.getBitIdentifier() << localeBitOffset) != 0) {

                    // valid only if localeBitOffset is 0.
                    languageAvailability |= ContractAssetType.AUDIO.getBitIdentifier(); // value: 001

                }
            } else {
                if ((contractAvailability & ContractAssetType.AUDIO.getBitIdentifier() << localeBitOffset) != 0) {

                    // valid only of localeBitOffset is 0
                    languageAvailability |= ContractAssetType.AUDIO.getBitIdentifier(); // value: 001
                }

            }
        }

        if (checkText) {
            TimedTextTypeDescriptor textType = descriptorData.timedTextType;

            if (textType != null && !Arrays.equals(FORCED_CHARS, textType.nameStr)) {
                if (languageMatches(language, descriptorData.textLanguage)) {
                    int localeBitOffset = ContractAssetType.values().length * languageDialectOffset(language, descriptorData.textLanguage);

                    if ((contractAvailability & ContractAssetType.SUBTITLES.getBitIdentifier() << localeBitOffset) != 0) {

                        // valid only if localeBitOffset is 0
                        languageAvailability |= ContractAssetType.SUBTITLES.getBitIdentifier(); // value: 010 for only SUBTITLES, or 011 for AUDIO & SUBTITLES
                    }
                }
            }
        }

        // returns int 1 (AUDIO ONLY), 2 (SUBTITLES ONLY) or 3 (AUDIO AND SUBTITLES)
        return languageAvailability;
    }

    /**
     * If descriptor language (audio or text) matches "zxx".
     *
     * @param descriptor
     * @param checkAudio
     * @param checkText
     * @return
     */
    private boolean noLanguageIsAvailable(EncodeSummaryDescriptor descriptor, boolean checkAudio, boolean checkText) {
        EncodeSummaryDescriptorData descriptorData = descriptor.descriptorData;

        if (checkAudio && !languageMatches("zxx", descriptorData.audioLanguage))
            return false;// does not match "zxx"

        if (checkText) {
            return !languageMatches("zxx", descriptorData.textLanguage);// return false, if does not match "zxx"
        }

        return true;
    }

    /**
     * Check if given language is a prefix or equals audioOrTextLanguage
     *
     * @param language
     * @param audioOrTextLanguage language of EncodingSummaryDescriptor
     * @return true if language prefix or equals audio
     */
    private boolean languageMatches(String language, Strings audioOrTextLanguage) {
        if (audioOrTextLanguage == null)
            return false;

        // if audioOrTextLanguage is en and language is zh-Hant. languages do not match return false.
        if (audioOrTextLanguage.value.length < language.length())
            return false;

        // cases where language is es and audioOrTextLanguage is en (same length)
        // cases where language is fr and audioOrTextLanguage is zh-Hant

        // this loop checks language is a prefix of audioOrTextLanguage.
        for (int i = 0; i < language.length(); i++) {
            if (language.charAt(i) != audioOrTextLanguage.value[i])
                return false;
        }

        // return true if language is a prefix of audioOrTextLanguage or equals each other.
        // examples language: pt audioOrTextLanguage: pt-BR -> (prefix of audioOrTextLanguage) do we have such a use case?
        // language: language en audioOrTextLanguage: en -> or they equal.
        return true;
    }

    private int languageDialectOffset(String language, Strings locale) {
        if (locale.value.length == language.length())
            return 0;

        return dialectOrdinalAssigner.getDialectOrdinal(language, String.valueOf(locale.value));
    }

    private int languageDialectOffset(String language, String locale) {
        if (locale.length() == language.length())
            return 0;

        return dialectOrdinalAssigner.getDialectOrdinal(language, locale);
    }

}
