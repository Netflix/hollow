package com.netflix.vms.transformer.modules.artwork;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
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
                .compare(o1.locale, o2.locale, Ordering.usingToString())
                .compare(o1.seqNum, o2.seqNum)
                .compare(o1.sourceFileId.toString(), o2.sourceFileId.toString());
        int result = chain.result();
        return result;
    }

    private int compateArtWorkOrdinalPriority(final Artwork o1, Artwork o2) {
        final Integer t1 = Integer.valueOf(o1.ordinalPriority);
        final Integer t2 = Integer.valueOf(o2.ordinalPriority);
        return t1.compareTo(t2);
    }

    private int compareArtWorkEffectiveDate(final Artwork o1, Artwork o2) {
        // Current artwork with most recent effective date should be on the top.
        //        final Long now = VMSDataClock.getInstance().currentTimeMillis();
        final Long now = Long.valueOf(ctx.getNowMillis());

        final Long t1 = Long.valueOf(o1.effectiveDate);
        final Long t2 = Long.valueOf(o2.effectiveDate);

        return (t1 < now && t2 < now) ? t2.compareTo(t1) : t1.compareTo(t2);
    }
}