package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.common.TransformerContext;
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

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage, TimecodeAnnotationHollow inputTimecodeAnnotation, TransformerContext ctx) {
        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if (packageMomentData != null)
            return packageMomentData;
        
        // Determine if timecode annotation is enabled
        if(ctx.getConfig().isTimecodeAnnotationFeedEnabled()) {
            packageMomentData = findStartAndEndMomentOffsets(inputTimecodeAnnotation);        	
        } else {
        	packageMomentData = findStartAndEndMomentOffsets(inputPackage);
        }
        packageMomentDataByPackageId.put(Integer.valueOf(packageData.id), packageMomentData);
        return packageMomentData;
    }

    private PackageMomentData findStartAndEndMomentOffsets(PackageHollow inputPackage) {
        PackageMomentData data = new PackageMomentData();

        PackageMomentListHollow moments = inputPackage._getMoments();

        if (moments != null) {
            boolean startFound = false;
            boolean endFound = false;

            for (PackageMomentHollow packageMoment : inputPackage._getMoments()) {
                String momentType = packageMoment._getMomentType()._getValue();

                if ("Start".equals(momentType)) {
                    long offsetMillis = packageMoment._getOffsetMillis();
                    data.startMomentOffsetInMillis = offsetMillis;
                    if (endFound)
                        break;
                    startFound = true;
                } else if ("Ending".equals(momentType)) {
                    long offsetMillis = packageMoment._getOffsetMillis();
                    data.endMomentOffsetInMillis = offsetMillis;
                    if (startFound)
                        break;
                    endFound = true;
                }
            }
        }
        return data;
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