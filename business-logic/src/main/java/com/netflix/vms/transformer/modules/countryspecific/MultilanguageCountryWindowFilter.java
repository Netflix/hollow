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
    
    public final InputOrdinalResultCache<ContractAsset> rightsContractAssetCache;
    
    public MultilanguageCountryWindowFilter(CycleConstants cycleConstants) {
        this.rightsContractAssetCache = cycleConstants.rightsContractAssetCache;
    }
    
    /**
     * Returns an integer describing the asset type rights available for the specified language. 
     * 
     * @param language
     * @param contract
     * @return
     */
    public int contractAvailabilityForLanguage(String language, RightsContractHollow contract) {
        int availability = 0;
        
        for(RightsContractAssetHollow assetInput : contract._getAssets()) {
            ContractAsset asset = rightsContractAssetCache.getResult(assetInput.getOrdinal());
            if(asset == null) {
                asset = new ContractAsset(assetInput);
                rightsContractAssetCache.setResult(assetInput.getOrdinal(), asset);
            }
            
            if(language.equals(asset.getLanguage())) {
                availability |= asset.getType().getBitIdentifier();
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
    
    public boolean packageIsAvailableForLanguage(String language, PackageData pkg, int languageAvailability) {
        boolean anyLanguageDiscovered = false;
        
        for(EncodeSummaryDescriptor descriptor : pkg.muxAudioStreamSummary) {
            if(languageIsAvailable(language, descriptor, languageAvailability, true, true))
                return true;
            
            if(!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, true, true))
                anyLanguageDiscovered = true;
        }
        
        for(EncodeSummaryDescriptor descriptor : pkg.audioStreamSummary) {
            if(languageIsAvailable(language, descriptor, languageAvailability, true, false))
                return true;
            
            if(!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, true, false))
                anyLanguageDiscovered = true;
        }
        
        for(EncodeSummaryDescriptor descriptor : pkg.textStreamSummary) {
            if(languageIsAvailable(language, descriptor, languageAvailability, false, true))
                return true;
            
            if(!anyLanguageDiscovered && !noLanguageIsAvailable(descriptor, false, true))
                anyLanguageDiscovered = true;
        }
        
        return !anyLanguageDiscovered;
    }
    
    private static final char[] FORCED_CHARS = "Forced".toCharArray();
    
    private boolean languageIsAvailable(String language, EncodeSummaryDescriptor descriptor,  int languageAvailability, boolean checkAudio, boolean checkText) {
        EncodeSummaryDescriptorData descriptorData = descriptor.descriptorData;
        
        if(checkAudio && languageMatches(language, descriptorData.audioLanguage)) {
            if(descriptorData.assetType.id == 2) {
                if((languageAvailability & ContractAssetType.DESCRIPTIVE_AUDIO.getBitIdentifier()) != 0)
                    return true;
            } else {
                if((languageAvailability & ContractAssetType.AUDIO.getBitIdentifier()) != 0)
                    return true;
            }
        }
        
        if(checkText) {
            TimedTextTypeDescriptor textType = descriptorData.timedTextType;
            
            if(textType != null && !Arrays.equals(FORCED_CHARS, textType.nameStr)) {
                if(languageMatches(language, descriptorData.textLanguage)) {
                    if((languageAvailability & ContractAssetType.SUBTITLES.getBitIdentifier()) != 0)
                        return true;
                }
            }
        }
        
        return false;
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
    
    private static boolean languageMatches(String language, Strings locale) {
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

}
