package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.ListOfCertificationHollow;
import com.netflix.vms.transformer.common.TransformerContext;

public class CertificationSystemCircuitBreaker extends HollowPerCountryCompleteVideoScoringCircuitBreaker {

    private final int maxMaturityRating;

    public CertificationSystemCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
        this(ctx, vip, versionId, Integer.MAX_VALUE);
    }

    public CertificationSystemCircuitBreaker(TransformerContext ctx, String vip, long versionId, int maxMaturityRating) {
        super(ctx, vip, versionId);
        this.maxMaturityRating = maxMaturityRating;
    }

    @Override
    public String getRuleName() {
        if (maxMaturityRating == Integer.MAX_VALUE)
            return "CertificationSystemCheck";
        return "CertificationSystemCheckMax" + maxMaturityRating;
    }

    @Override
    protected int getVideoScore(CompleteVideoHollow cv) {
        ListOfCertificationHollow certList = cv._getData()._getCountrySpecificData()._getCertificationList();

        if (certList == null)
            return 0;

        /// If we're just counting ratings, let's count all of them.
        if (maxMaturityRating == Integer.MAX_VALUE)
            return certList.size();

        /// Otherwise, per: https://confluence.netflix.com/display/MERCHINF/Puneet%27s+writeups+on+maturity+level
        /// Only the first maturity rating in the certification list for a movie determines filtering logic
        /// so let's count the movies in which the first rating is not filtered out by this maturity rating.
        if (certList.size() > 0) {
            int maturityLevel = certList.get(0)._getMovieCert()._getMaturityLevel();
            if (maturityLevel <= maxMaturityRating)
                return 1;
        }

        return 0;
    }


}
