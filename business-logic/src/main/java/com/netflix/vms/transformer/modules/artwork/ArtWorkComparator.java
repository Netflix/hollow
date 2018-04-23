package com.netflix.vms.transformer.modules.artwork;

import com.google.common.collect.ComparisonChain;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowoutput.Artwork;
import java.util.Comparator;

public class ArtWorkComparator implements Comparator<Artwork> {
    private Boolean _isOrdinalPriorityForTestingEnabled;
    private final TransformerContext ctx;

    public ArtWorkComparator(TransformerContext ctx) {
        this.ctx = ctx;
    }

    protected void setOrdinalPriorityForTestingEnabled(boolean flag) {
        _isOrdinalPriorityForTestingEnabled = flag;
    }

    protected boolean isArtWorkOrderPriorityEnabled() {
        if (_isOrdinalPriorityForTestingEnabled != null) return _isOrdinalPriorityForTestingEnabled;
        return true;
        //        return MetaDataCommonPropertyManager.isArtWorkOrderPriorityEnabled();
    }

    @Override
    public int compare(Artwork o1, Artwork o2) {
        // Order by effective data first
        final int effectiveDateResult = compareArtWorkEffectiveDate(o1, o2);
        if (effectiveDateResult != 0) return effectiveDateResult;

        // Then ordinal priority
        if (isArtWorkOrderPriorityEnabled()) {
            final int ordinalResult = compateArtWorkOrdinalPriority(o1, o2);
            if (ordinalResult != 0) return ordinalResult;
        }

        // The rest
        final ComparisonChain chain =
                ComparisonChain.start()
                .compare(o1.locale, o2.locale)
                .compare(o1.seqNum, o2.seqNum)
                .compare(o1.sourceFileId, o2.sourceFileId);
        int result = chain.result();
        return result;
    }

    private int compateArtWorkOrdinalPriority(final Artwork o1, Artwork o2) {
        int t1 = o1.ordinalPriority;
        int t2 = o2.ordinalPriority;
        return t1 < t2 ? -1 : (t1 == t2 ? 0 : 1);
    }

    private int compareArtWorkEffectiveDate(final Artwork o1, Artwork o2) {
        // Current artwork with most recent effective date should be on the top.
        long now = ctx.getNowMillis();

        long t1 = o1.effectiveDate;
        long t2 = o2.effectiveDate;

        return (t1 < now && t2 < now) ? (t2 < t1 ? -1 : (t2 == t1 ? 0 : 1)) : t1 < t2 ? -1 : (t1 == t2 ? 0 : 1);
    }
}