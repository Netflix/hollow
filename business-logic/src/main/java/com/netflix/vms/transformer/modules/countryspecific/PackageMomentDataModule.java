package com.netflix.vms.transformer.modules.countryspecific;

import com.netflix.vms.transformer.hollowinput.PackageHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentHollow;
import com.netflix.vms.transformer.hollowinput.PackageMomentListHollow;
import com.netflix.vms.transformer.hollowoutput.PackageData;

import java.util.HashMap;
import java.util.Map;

public class PackageMomentDataModule {

    private final Map<Integer, PackageMomentData> packageMomentDataByPackageId;

    public PackageMomentDataModule() {
        this.packageMomentDataByPackageId = new HashMap<>();
    }

    public PackageMomentData getWindowPackageMomentData(PackageData packageData, PackageHollow inputPackage) {
        PackageMomentData packageMomentData = packageMomentDataByPackageId.get(Integer.valueOf(packageData.id));
        if (packageMomentData != null)
            return packageMomentData;
        packageMomentData = findStartAndEndMomentOffsets(inputPackage);
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

    public void reset() {
        this.packageMomentDataByPackageId.clear();
    }

}