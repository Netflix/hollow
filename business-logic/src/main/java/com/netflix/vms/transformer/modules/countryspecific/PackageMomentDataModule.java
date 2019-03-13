package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.StringHollow;
import com.netflix.vms.transformer.hollowinput.TimecodeAnnotationsListHollow;
import com.netflix.vms.transformer.hollowinput.TimecodedMomentAnnotationHollow;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.Strings;
import com.netflix.vms.transformer.hollowoutput.TimecodeAnnotation;

import java.util.HashMap;
import java.util.Map;

public class PackageMomentDataModule {

	private static final String DEFAULT_ENCODING_ALGORITHM = "default";
    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;

    public PackageMomentDataModule() {
        this.packageMomentDataByPackageId = new HashMap<>();
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage) {

        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if (packageMomentData != null)
            return packageMomentData;        
        packageMomentData = createPackageMomentData(inputPackage);        	        	
        packageMomentDataByPackageId.put(Integer.valueOf(packageData.id), packageMomentData);
        return packageMomentData;
    }
    
    private PackageMomentData createPackageMomentData(PackageHollow inputPackage) {
    	PackageMomentData data = new PackageMomentData();
    	
    	if(inputPackage != null) {
    		TimecodeAnnotationsListHollow moments = inputPackage._getTimecodeAnnotations();
    		
    		if(moments != null) {
    			for(TimecodedMomentAnnotationHollow moment : moments) {
    				// If we find start or end moment, record that as well
    				if(moment._getType()._getValue().equals("Start"))
    					data.startMomentOffsetInMillis = moment._getStartMillis();
    				if(moment._getType()._getValue().equals("Ending"))
    					data.endMomentOffsetInMillis = moment._getStartMillis();
    				    				
        			TimecodeAnnotation annotation = new TimecodeAnnotation();
        			annotation.type = moment._getType()._getValue().toCharArray();
        			annotation.startMillis = moment._getStartMillis();
        			annotation.endMillis = moment._getEndMillis();
        			StringHollow algo = moment._getEncodingAlgorithmHash();
        			if(algo != null)
       					annotation.encodingAlgorithmHash = new Strings(algo._getValue());
        			else
        				annotation.encodingAlgorithmHash = new Strings(DEFAULT_ENCODING_ALGORITHM);
        			data.timecodes.add(annotation);    				
    			}
    		}

    	}
    	
    	return data;
    }
    


    public void reset() {
        this.packageMomentDataByPackageId.clear();
    }

}