package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.i18n.NFLocale;
import com.netflix.vms.generated.notemplate.*;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import java.util.*;

/**
 * Circuit breaker to check catalog sizes
 */
public class CatalogSizeCircuitBreaker extends HollowCircuitBreaker {

    private static final String RULE_NAME = "catalog_size";

    public CatalogSizeCircuitBreaker(PublishWorkflowContext ctx, long versionId) {
        super(ctx, versionId);
    }

    @Override
    public String getRuleName() {
        return RULE_NAME;
    }

    @Override
    public boolean isCountrySpecific() {
        return super.isCountrySpecific();
    }

    @Override
    protected CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        CircuitBreakerResults results = new CircuitBreakerResults();
        HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(stateEngine, "MulticatalogCountryData", "videoId.value", "country!");
        VMSRawHollowAPI hollowApi = new VMSRawHollowAPI(stateEngine);

        Map<String, Integer> countryCatalogSize = new HashMap<>();
        Map<String, Map<String, Integer>> countryLanguageCatalogSize = new HashMap<>();

        Set<String> countries = ctx.getOctoberSkyData().getMultiLanguageCatalogCountries();
        Collection<CompleteVideoHollow> allCompleteVideoHollow = hollowApi.getAllCompleteVideoHollow();
        for (CompleteVideoHollow completeVideoHollow : allCompleteVideoHollow) {

            for (String country : countries) {

                countryCatalogSize.putIfAbsent(country, 0);
                boolean isAvailableForEd = isAvailableForED(completeVideoHollow, country, null, idx, hollowApi);
                if (isAvailableForEd) {
                    int count = countryCatalogSize.get(country);
                    countryCatalogSize.put(country, count + 1);
                }

                Set<String> languages = ctx.getOctoberSkyData().getCatalogLanguages(country);
                for (String language : languages) {

                    countryLanguageCatalogSize.putIfAbsent(country, new HashMap<>());
                    Map<String, Integer> languageCatalogSize = countryLanguageCatalogSize.get(country);
                    languageCatalogSize.putIfAbsent(language, 0);

                    isAvailableForEd = isAvailableForED(completeVideoHollow, country, language, idx, hollowApi);
                    if (isAvailableForEd) {
                        int count = languageCatalogSize.get(country);
                        languageCatalogSize.put(country, count + 1);
                    }
                }
            }
        }

        for (String country : countries) {

            StringBuilder message = new StringBuilder();
            message.append(country + " size : " + countryCatalogSize.get(country)).append(", ");

            Set<String> languages = ctx.getOctoberSkyData().getCatalogLanguages(country);
            for (String language : languages) {
                message.append(country).append(":").append(language)
                        .append(" size : ").append(countryLanguageCatalogSize.get(country).get(language))
                        .append(",");
            }
            ctx.getLogger().log(TaggingLogger.Severity.INFO, Collections.singleton(TransformerLogTag.Catalog_Size), message.toString());
        }

        return results;
    }

    boolean isAvailableForED(CompleteVideoHollow completeVideoHollow, String country, String language, HollowPrimaryKeyIndex idx, VMSRawHollowAPI api) {
        boolean isGoLive = completeVideoHollow._getData()._getFacetData()._getVideoMediaData()._getIsGoLive();
        if (isGoLive) { return false; }

        if (language != null) {
            int multiCatalogCountryDataOrdinal;
            long videoId = completeVideoHollow._getId()._getValue();
            multiCatalogCountryDataOrdinal = idx.getMatchingOrdinal(videoId, country);
            if (multiCatalogCountryDataOrdinal != -1) {
                MulticatalogCountryDataHollow multicatalogCountryDataHollow = api.getMulticatalogCountryDataHollow(multiCatalogCountryDataOrdinal);
                MapOfNFLocaleToMulticatalogCountryLocaleDataHollow map = multicatalogCountryDataHollow._getLanguageData();
                if (!map.isEmpty() && map.containsKey(country)) {
                    NFLocale locale = NFLocale.findInstance(language);
                    if (locale != null) {
                        MulticatalogCountryLocaleDataHollow localeDataHollow = map.get(locale);
                        ListOfVMSAvailabilityWindowHollow availabilityWindowListHollow = localeDataHollow._getAvailabilityWindows();

                        ListIterator<VMSAvailabilityWindowHollow> it = availabilityWindowListHollow.listIterator();
                        while (it.hasNext()) {
                            VMSAvailabilityWindowHollow availabilityWindowHollow = it.next();
                            long start = availabilityWindowHollow._getStartDate()._getVal();
                            long end = availabilityWindowHollow._getEndDate()._getVal();

                            if (start <= ctx.getNowMillis() && ctx.getNowMillis() < end) { return true; }
                        }
                    }
                }
            }

            return false;

        } else {
            List<VMSAvailabilityWindowHollow> availabilityWindowsHollow = completeVideoHollow._getData()._getCountrySpecificData()._getAvailabilityWindows();
            if (availabilityWindowsHollow == null || availabilityWindowsHollow.isEmpty()) { return false; }

            for (VMSAvailabilityWindowHollow window : availabilityWindowsHollow) {

                long start = window._getStartDate()._getVal();
                long end = window._getEndDate()._getVal();
                if (start <= ctx.getNowMillis() && ctx.getNowMillis() < end) { return true; }
            }

            return false;
        }
    }
}
