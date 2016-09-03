package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.contract.ContractAssetType;

import com.netflix.vms.transformer.contract.ContractAsset;
import com.netflix.vms.transformer.util.InputOrdinalResultCache;
import com.netflix.vms.transformer.CycleConstants;
import com.netflix.vms.transformer.hollowinput.RightsContractAssetHollow;
import com.netflix.vms.transformer.hollowinput.RightsContractHollow;
import com.netflix.vms.transformer.hollowoutput.EncodeSummaryDescriptor;
import com.netflix.vms.transformer.hollowoutput.EncodeSummaryDescriptorData;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TimedTextTypeDescriptor;
import java.util.Arrays;

public class MultilanguageCountryWindowFilter {
    
    private final InputOrdinalResultCache<ContractAsset> rightsContractAssetCache;
    private final MultilanguageCountryDialectOrdinalAssigner dialectOrdinalAssigner;
    
    
    public MultilanguageCountryWindowFilter(CycleConstants cycleConstants) {
        this.rightsContractAssetCache = cycleConstants.rightsContractAssetCache;
        this.dialectOrdinalAssigner = cycleConstants.dialectOrdinalAssigner;
    }
    
    /**
     * Returns an integer describing the asset type rights available for the specified language. 
     * 
     * @param language
     * @param contract
     * @return
     */
    public long contractAvailabilityForLanguage(String language, RightsContractHollow contract) {
        long availability = 0;
        
        for(RightsContractAssetHollow assetInput : contract._getAssets()) {
            ContractAsset asset = rightsContractAssetCache.getResult(assetInput.getOrdinal());
            if(asset == null) {
                asset = new ContractAsset(assetInput);
                rightsContractAssetCache.setResult(assetInput.getOrdinal(), asset);
            }
            
            if(language.equals(asset.getLanguage())) {
                int localeBitOffset = ContractAssetType.values().length * languageDialectOffset(language, asset.getLocale());
                availability |= asset.getType().getBitIdentifier() << localeBitOffset;
            }
        }
        
        return availability;
    }
    
    /**
     * Determines if the language is available for the given package, assuming the specified asset type rights 
     * 
     * @param language
     * @param pkg
     * @param languageAvailability The asset type rights as returned from contractAvailabilityForLangauge()
     * @return
     */
    public long packageIsAvailableForLanguage(String language, PackageData pkg, long languageAvailability) {
        if(pkg == null)
            return 0;
        
        long packageAvailability = 0;
        
        boolean anyLanguageDiscovered = false;
        
        for(EncodeSummaryDescriptor descriptor : pkg.muxAudioStreamSummary) {
            packageAvailability |= languageIsAvailable(language, descriptor, languageAvailability, true, true);
            if(packageAvailability == (ContractAssetType.AUDIO.getBitIdentifier() | ContractAssetType.SUBTITLES.getBitIdentifier()))
                return packageAvailability;
            
            if(!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, true, true))
                anyLanguageDiscovered = true;
        }
        
        if((packageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) == 0) {
            for(EncodeSummaryDescriptor descriptor : pkg.audioStreamSummary) {
                packageAvailability |= languageIsAvailable(language, descriptor, languageAvailability, true, false);
                if((packageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) != 0)
                    break;
                
                if(!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, true, false))
                    anyLanguageDiscovered = true;
            }
        }
        
        if((packageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) == 0) {
            for(EncodeSummaryDescriptor descriptor : pkg.textStreamSummary) {
                packageAvailability |= languageIsAvailable(language, descriptor, languageAvailability, false, true);
                if((packageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) != 0)
                    break;
                
                if(!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, false, true))
                    anyLanguageDiscovered = true;
            }
        }
        
        if(!anyLanguageDiscovered)
            return Long.MIN_VALUE;
        return packageAvailability;
    }
    
    private static final char[] FORCED_CHARS = "Forced".toCharArray();
    
    private long languageIsAvailable(String language, EncodeSummaryDescriptor descriptor,  long contractAvailability, boolean checkAudio, boolean checkText) {
        EncodeSummaryDescriptorData descriptorData = descriptor.descriptorData;
        
        long languageAvailability = 0;
        
        if(checkAudio && languageMatches(language, descriptorData.audioLanguage)) {
            int localeBitOffset = ContractAssetType.values().length * languageDialectOffset(language, descriptorData.audioLanguage);
            
            if(descriptorData.assetType.id == 2) {
                if((contractAvailability & ContractAssetType.DESCRIPTIVE_AUDIO.getBitIdentifier() << localeBitOffset) != 0) {
                    languageAvailability |= ContractAssetType.AUDIO.getBitIdentifier();
                }
            } else {
                if((contractAvailability & ContractAssetType.AUDIO.getBitIdentifier() << localeBitOffset) != 0) {
                    languageAvailability |= ContractAssetType.AUDIO.getBitIdentifier();
                }
                
            }
        }
        
        if(checkText) {
            TimedTextTypeDescriptor textType = descriptorData.timedTextType;
            
            if(textType != null && !Arrays.equals(FORCED_CHARS, textType.nameStr)) {
                if(languageMatches(language, descriptorData.textLanguage)) {
                    int localeBitOffset = ContractAssetType.values().length * languageDialectOffset(language, descriptorData.textLanguage);
                    
                    if((contractAvailability & ContractAssetType.SUBTITLES.getBitIdentifier() << localeBitOffset) != 0) {
                        languageAvailability |= ContractAssetType.SUBTITLES.getBitIdentifier();
                    }
                }
            }
        }
        
        return languageAvailability;
    }
    
    private boolean noLanguageIsAvailable(EncodeSummaryDescriptor descriptor, boolean checkAudio, boolean checkText) {
        EncodeSummaryDescriptorData descriptorData = descriptor.descriptorData;
        
        if(checkAudio && !languageMatches("zxx", descriptorData.audioLanguage))
            return false;
        
        if(checkText) {
            return !languageMatches("zxx", descriptorData.textLanguage);
        }
        
        return true;
    }    
    
    private boolean languageMatches(String language, Strings locale) {
        if(locale == null)
            return false;
        
        if(locale.value.length < language.length())
            return false;
        
        for(int i=0;i<language.length();i++) {
            if(language.charAt(i) != locale.value[i])
                return false;
        }
        
        return true;
    }
    
    private int languageDialectOffset(String language, Strings locale) {
        if(locale.value.length == language.length())
            return 0;
        
        return dialectOrdinalAssigner.getDialectOrdinal(language, String.valueOf(locale.value));
    }

    private int languageDialectOffset(String language, String locale) {
        if(locale.length() == language.length())
            return 0;
        
        return dialectOrdinalAssigner.getDialectOrdinal(language, locale);
    }

}
