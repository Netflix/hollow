package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.TimecodeAnnotationsListHollow;
import com.netflix.vms.transformer.hollowinput.TimecodedMomentAnnotationHollow;
import com.netflix.vms.transformer.hollowoutput.PackageData;
import com.netflix.vms.transformer.hollowoutput.TimecodeAnnotation;

import java.util.HashMap;
import java.util.Map;

public class PackageMomentDataModule {

    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;
    private final TransformerConfig config;

    public PackageMomentDataModule(TransformerConfig config) {
        this.packageMomentDataByPackageId = new HashMap<>();
        this.config = config;
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage,  TransformerContext ctx) {

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