package com.netflix.vms.transformer.modules.artwork;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.netflix.vms.transformer.hollowoutput.ArtWorkDescriptor;
import com.netflix.vms.transformer.hollowoutput.AssetLocation;
import com.netflix.vms.transformer.hollowoutput.Strings;

import java.util.Comparator;
import java.util.Map;

public class ArtWorkComparator implements Comparator<ArtWorkDescriptor>{
    private Boolean _isOrdinalPriorityForTestingEnabled;
    private static final FilenameComparator filenameComparator = new FilenameComparator();

    protected void setOrdinalPriorityForTestingEnabled(boolean flag) {
        _isOrdinalPriorityForTestingEnabled = flag;
    }

    protected boolean isArtWorkOrderPriorityEnabled() {
        if (_isOrdinalPriorityForTestingEnabled != null) return _isOrdinalPriorityForTestingEnabled;
        return true;
//        return MetaDataCommonPropertyManager.isArtWorkOrderPriorityEnabled();
    }

    @Override
    public int compare(ArtWorkDescriptor o1, ArtWorkDescriptor o2) {
        // Order by effective data first
        final int effectiveDateResult = compareArtWorkEffectiveDate(o1, o2);
        if (effectiveDateResult != 0) return effectiveDateResult;

        // Then ordinal priority
        if (isArtWorkOrderPriorityEnabled()) {
            final int ordinalResult = compateArtWorkOrdinalPriority(o1, o2);
            if (ordinalResult != 0) return ordinalResult;
        }

//        String sfId1 = o1.getSourceFileId();
//        if (sfId1 == null) sfId1 = "";
//        String sfId2 = o2.getSourceFileId();
//        if (sfId2 == null) sfId2 = "";

        // The rest
        final ComparisonChain chain =
                ComparisonChain.start()
                .compare(o1.locale, o2.locale, Ordering.usingToString())
                .compare(Boolean.valueOf(o1.isUsDescriptor), Boolean.valueOf(o2.isUsDescriptor))
                .compare(new String(o1.imageType.nameStr), new String(o2.imageType.nameStr), Ordering.natural().nullsFirst())
                .compare(o1.seqNum, o2.seqNum)
                .compare(new String(o1.format.nameStr), new String(o2.format.nameStr), Ordering.natural().nullsFirst())
                .compare(o1.imageId, o2.imageId)
//                .compare(sfId1, sfId2)
                .compare(o1.assetLocationMap, o2.assetLocationMap, filenameComparator);
        int result = chain.result();
        return result;
    }

    private static class FilenameComparator implements Comparator<Map<Strings, AssetLocation>> {
        @Override
        public int compare(Map<Strings, AssetLocation> o1, Map<Strings, AssetLocation> o2) {
            if (o1 == o2) return 0;
            if (o1 == null) return -1;
            if (o2 == null) return 1;
            if (o1.isEmpty() == o2.isEmpty()) return 0;

            Integer s1 = o1.size();
            Integer s2 = o2.size();
            int result = s1.compareTo(s2);
            if (result == 0) {
                if (o1.equals(o2)) return 0;

                // @TODO: better way to compare Map<String> ?
                String str1 = o1.toString();
                String str2 = o2.toString();
                result = str1.compareTo(str2);
            }
            return result;
        }

    }

    private int compateArtWorkOrdinalPriority(final ArtWorkDescriptor o1, ArtWorkDescriptor o2) {
        final Integer t1 = Integer.valueOf(o1.ordinalPriority);
        final Integer t2 = Integer.valueOf(o2.ordinalPriority);
        return t1.compareTo(t2);
    }

    private int compareArtWorkEffectiveDate(final ArtWorkDescriptor o1, ArtWorkDescriptor o2) {
        // Current artwork with most recent effective date should be on the top.
//        final Long now = VMSDataClock.getInstance().currentTimeMillis();
        final Long now = Long.valueOf(System.currentTimeMillis());

        final Long t1 = Long.valueOf(o1.effectiveDate);
        final Long t2 = Long.valueOf(o2.effectiveDate);

        return (t1 < now && t2 < now) ? t2.compareTo(t1) : t1.compareTo(t2);
    }
}