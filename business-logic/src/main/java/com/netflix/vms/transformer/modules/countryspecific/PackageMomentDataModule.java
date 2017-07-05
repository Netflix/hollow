package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentListHollow;
import com.netflix.vms.transformer.hollowinput.TimecodeAnnotationHollow;
import com.netflix.vms.transformer.hollowinput.TimecodeAnnotationsListHollow;
import com.netflix.vms.transformer.hollowinput.TimecodedMomentAnnotationHollow;
import com.netflix.vms.transformer.hollowoutput.PackageData;

import java.util.HashMap;
import java.util.Map;

public class PackageMomentDataModule {

    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;

    public PackageMomentDataModule() {
        this.packageMomentDataByPackageId = new HashMap<>();
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage, TimecodeAnnotationHollow inputTimecodeAnnotation) {
        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if (packageMomentData != null)
            return packageMomentData;
        packageMomentData = findStartAndEndMomentOffsets(inputTimecodeAnnotation);
        packageMomentDataByPackageId.put(Integer.valueOf(packageData.id), packageMomentData);
        return packageMomentData;
    }

    private PackageMomentData findStartAndEndMomentOffsets(TimecodeAnnotationHollow inputTimecodeAnnotation) {
        PackageMomentData data = new PackageMomentData();
        
        if(inputTimecodeAnnotation != null) {
        	// Get the list of moments
        	TimecodeAnnotationsListHollow moments = inputTimecodeAnnotation._getTimecodeAnnotations();
        	if(moments != null) {
                boolean startFound = false;
                boolean endFound = false;

        		for(TimecodedMomentAnnotationHollow moment : moments) {
        			String momentType = moment._getType()._getValue();
        			if("Start".equals(momentType)) {
        				data.startMomentOffsetInMillis = moment._getStartMillis();
        				if(endFound)
        					break;
        				startFound = true;
        			} else if("Ending".equals(momentType)) {
        				data.endMomentOffsetInMillis = moment._getEndMillis();
        				if(startFound)
        					break;
        				endFound = true;
        			}
        		}
        	}
        }
        return data;
    }

    public void reset() {
        this.packageMomentDataByPackageId.clear();
    }

}