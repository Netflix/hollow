package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.index.HollowHashIndex;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import java.util.HashMap;
import java.util.Map;


/**
 * Circuit breaker to check catalog sizes
 */
public class CatalogSizeCircuitBreaker extends HollowCircuitBreaker {

    private final String ruleName;

    public CatalogSizeCircuitBreaker(PublishWorkflowContext ctx, long versionId, String ruleName) {
        super(ctx, versionId);
        this.ruleName = ruleName;
    }

    @Override
    public String getRuleName() {
        return ruleName;
    }

    @Override
    public boolean isCountrySpecific() {
        return true;
    }

    @Override
    protected CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        CircuitBreakerResults results = new CircuitBreakerResults();
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(stateEngine, "MulticatalogCountryData", "videoId.value", "country.id");
        HollowHashIndex completeVideoIdx = new HollowHashIndex(stateEngine, "CompleteVideo", "", "country.id");
        com.netflix.vms.generated.notemplate.VMSRawHollowAPI hollowApi = new com.netflix.vms.generated.notemplate.VMSRawHollowAPI(stateEngine);

        Map<String, Integer> countryCatalogSize = new HashMap<>();
        Map<String, Map<String, Integer>> countryLanguageCatalogSize = new HashMap<>();

        java.util.Set<String> countries = ctx.getOctoberSkyData().getMultiLanguageCatalogCountries();


        for (String country : countries) {
            countryCatalogSize.putIfAbsent(country, 0);

            HollowHashIndexResult hashIndexResult = completeVideoIdx.findMatches(country);
            if (hashIndexResult != null) {

                countryLanguageCatalogSize.putIfAbsent(country, new HashMap<>());
                HollowOrdinalIterator iterator = hashIndexResult.iterator();
                int ordinal = iterator.next();
                while (ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {

                    com.netflix.vms.generated.notemplate.CompleteVideoHollow completeVideoHollow = hollowApi.getCompleteVideoHollow(ordinal);

                    countryCatalogSize.putIfAbsent(country, 0);
                    boolean isAvailableForEd = isAvailableForED(completeVideoHollow, country, null, idx, hollowApi);
                    if (isAvailableForEd) {
                        int count = countryCatalogSize.get(country);
                        countryCatalogSize.put(country, count + 1);
                    }

                    java.util.Set<String> languages = ctx.getOctoberSkyData().getCatalogLanguages(country);
                    for (String language : languages) {


                        Map<String, Integer> languageCatalogSize = countryLanguageCatalogSize.get(country);
                        languageCatalogSize.putIfAbsent(language, 0);

                        isAvailableForEd = isAvailableForED(completeVideoHollow, country, language, idx, hollowApi);
                        if (isAvailableForEd) {
                            int count = languageCatalogSize.get(language);
                            languageCatalogSize.put(language, count + 1);
                        }
                    }

                    ordinal = iterator.next();
                }
            } else {
                ctx.getLogger().error(TransformerLogTag.Catalog_Size, "HashIndexResult to find complete video for country={} is null", country);
            }
        }


        for (String country : countries) {

            StringBuilder message = new StringBuilder();
            message.append(country + " size : " + countryCatalogSize.get(country)).append(", ");

            java.util.Set<String> languages = ctx.getOctoberSkyData().getCatalogLanguages(country);
            for (String language : languages) {
                int count = 0;
                if (countryLanguageCatalogSize.get(country) != null && countryLanguageCatalogSize.get(country).get(language) != null) {
                    count = countryLanguageCatalogSize.get(country).get(language);
                }
                message.append(country).append(":").append(language)
                       .append(" size : ").append(count)
                       .append(",");
            }
            ctx.getLogger().info(java.util.Collections.singleton(TransformerLogTag.Catalog_Size), message.toString());
        }

        results.addResult(true, "Catalog size circuit breaker has passed");
        return results;
    }

    boolean isAvailableForED(com.netflix.vms.generated.notemplate.CompleteVideoHollow completeVideoHollow, String country, String language,
                             HollowPrimaryKeyIndex idx, com.netflix.vms.generated.notemplate.VMSRawHollowAPI api) {

        boolean isGoLive = completeVideoHollow._getData()._getFacetData()._getVideoMediaData()._getIsGoLive();
        if (!isGoLive) { return false; }

        if (language != null) {
            int multiCatalogCountryDataOrdinal;
            int videoId = completeVideoHollow._getId()._getValue();
            multiCatalogCountryDataOrdinal = idx.getMatchingOrdinal(videoId, country);
            if (multiCatalogCountryDataOrdinal != -1) {
                com.netflix.vms.generated.notemplate.MulticatalogCountryDataHollow
                        multicatalogCountryDataHollow = api.getMulticatalogCountryDataHollow(multiCatalogCountryDataOrdinal);
                com.netflix.vms.generated.notemplate.MapOfNFLocaleToMulticatalogCountryLocaleDataHollow map = multicatalogCountryDataHollow._getLanguageData();

                for (java.util.Map.Entry<com.netflix.vms.generated.notemplate.NFLocaleHollow, com.netflix.vms.generated.notemplate.MulticatalogCountryLocaleDataHollow> entry : map
                        .entrySet()) {
                    String nfLocale = entry.getKey()._getValue();
                    if (nfLocale.equals(language)) {
                        com.netflix.vms.generated.notemplate.MulticatalogCountryLocaleDataHollow localeDataHollow = entry.getValue();

                        com.netflix.vms.generated.notemplate.ListOfVMSAvailabilityWindowHollow availabilityWindowListHollow =
                                localeDataHollow._getAvailabilityWindows();
                        java.util.ListIterator<com.netflix.vms.generated.notemplate.VMSAvailabilityWindowHollow> it =
                                availabilityWindowListHollow.listIterator();

                        while (it.hasNext()) {
                            com.netflix.vms.generated.notemplate.VMSAvailabilityWindowHollow availabilityWindowHollow = it.next();
                            long start = availabilityWindowHollow._getStartDate()._getVal();
                            long end = availabilityWindowHollow._getEndDate()._getVal();

                            if (start <= ctx.getNowMillis() && ctx.getNowMillis() < end) { return true; }
                        }
                    }
                }

            }

            return false;

        } else {
            java.util.List<com.netflix.vms.generated.notemplate.VMSAvailabilityWindowHollow>
                    availabilityWindowsHollow = completeVideoHollow._getData()._getCountrySpecificData()._getAvailabilityWindows();
            if (availabilityWindowsHollow == null || availabilityWindowsHollow.isEmpty()) { return false; }

            for (com.netflix.vms.generated.notemplate.VMSAvailabilityWindowHollow window : availabilityWindowsHollow) {

                long start = window._getStartDate()._getVal();
                long end = window._getEndDate()._getVal();
                if (start <= ctx.getNowMillis() && ctx.getNowMillis() < end) { return true; }
            }

            return false;
        }
    }
}
