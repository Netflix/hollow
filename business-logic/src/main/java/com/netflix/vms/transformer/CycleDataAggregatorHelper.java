package com.netflix.vms.transformer;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_Catalog_Title_Availability;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_NoAssetRights;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_NoWindows;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_PrePromote;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_Skip_Contract_No_Assets;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.Language_catalog_WindowFiltered;

import com.netflix.vms.logging.TaggingLogger;

/**
 * Helper class to configure log tag messages and severity to aggregate data.
 */
public class CycleDataAggregatorHelper {

    // title is in pre-promotion phase using the new feed data
    public static final TaggingLogger.LogTag LANGUAGE_CATALOG_PRE_PROMOTION_TAG = Language_catalog_PrePromote;
    public static final String LANGUAGE_CATALOG_PRE_PROMOTION_MESSAGE = "Titles in Pre-promotion phase in country-language catalog";
    public static final TaggingLogger.Severity LANGUAGE_CATALOG_PRE_PROMOTION_SEVERITY = TaggingLogger.Severity.INFO;

    // title skipped contract since no localized assets were available - DROPPING
    public static final TaggingLogger.LogTag LANGUAGE_CATALOG_NO_LOCALIZED_ASSETS_TAG = Language_catalog_Skip_Contract_No_Assets;
    public static final String LANGUAGE_CATALOG_NO_LOCALIZED_ASSETS_MESSAGE = "Titles contract skipped because no localized assets were found";
    public static final TaggingLogger.Severity LANGUAGE_CATALOG_NO_LOCALIZED_ASSETS_SEVERITY = TaggingLogger.Severity.INFO;


    // titles that have zero windows in the catalog
    public static final TaggingLogger.LogTag LANGUAGE_CATALOG_NO_WINDOWS_TAG = Language_catalog_NoWindows;
    public static final String LANGUAGE_CATALOG_NO_WINDOWS_MESSAGE = "Titles for which no windows were found country-language catalog";
    public static final TaggingLogger.Severity LANGUAGE_CATALOG_NO_WINDOWS_SEVERITY = TaggingLogger.Severity.INFO;

    // if asset rights are not present in the feed
    public static final TaggingLogger.LogTag LANGUAGE_CATALOG_NO_ASSET_RIGHTS = Language_catalog_NoAssetRights;
    public static final String LANGUAGE_CATALOG_NO_ASSET_RIGHTS_MESSAGE = "Titles that do not have asset rights";
    public static final TaggingLogger.Severity LANGUAGE_CATALOG_NO_ASSET_RIGHTS_SEVERITY = TaggingLogger.Severity.INFO;

    // if window is filtered for unknown reasons
    public static final TaggingLogger.LogTag LANGUAGE_CATALOG_FILTER_WINDOW_TAG = Language_catalog_WindowFiltered;
    public static final String LANGUAGE_CATALOG_FILTER_WINDOW_MESSAGE = "Titles for asset rights are not present";
    public static final TaggingLogger.Severity LANGUAGE_CATALOG_FILTER_WINDOW_SEVERITY = TaggingLogger.Severity.INFO;

    // titles that do not meet merch requirements (only for for future titles where start window is in next 90 days and any title that has live window but fails the assets check)
    public static final TaggingLogger.LogTag LANGUAGE_CATALOG_TITLE_AVAILABILITY_TAG = Language_Catalog_Title_Availability;
    public static final String LANGUAGE_CATALOG_TITLE_AVAILABILITY_MESSAGE = "Future Titles (next 90 days) and current title that miss localized asset requirement check";
    public static final TaggingLogger.Severity LANGUAGE_CATALOG_TITLE_AVAILABILITY_SEVERITY = TaggingLogger.Severity.INFO;


    public static void configureLogsTagsForVMSWindowModule(CycleDataAggregator cycleDataAggregator) {
        cycleDataAggregator.aggregateForLogTag(LANGUAGE_CATALOG_PRE_PROMOTION_TAG, LANGUAGE_CATALOG_PRE_PROMOTION_SEVERITY, LANGUAGE_CATALOG_PRE_PROMOTION_MESSAGE);
        cycleDataAggregator.aggregateForLogTag(LANGUAGE_CATALOG_NO_LOCALIZED_ASSETS_TAG, LANGUAGE_CATALOG_NO_LOCALIZED_ASSETS_SEVERITY, LANGUAGE_CATALOG_NO_LOCALIZED_ASSETS_MESSAGE);
        cycleDataAggregator.aggregateForLogTag(LANGUAGE_CATALOG_FILTER_WINDOW_TAG, LANGUAGE_CATALOG_FILTER_WINDOW_SEVERITY, LANGUAGE_CATALOG_FILTER_WINDOW_MESSAGE);
        cycleDataAggregator.aggregateForLogTag(LANGUAGE_CATALOG_NO_WINDOWS_TAG, LANGUAGE_CATALOG_NO_WINDOWS_SEVERITY, LANGUAGE_CATALOG_NO_WINDOWS_MESSAGE);
        cycleDataAggregator.aggregateForLogTag(LANGUAGE_CATALOG_NO_ASSET_RIGHTS, LANGUAGE_CATALOG_NO_ASSET_RIGHTS_SEVERITY, LANGUAGE_CATALOG_NO_ASSET_RIGHTS_MESSAGE);
        cycleDataAggregator.aggregateForLogTag(LANGUAGE_CATALOG_TITLE_AVAILABILITY_TAG, LANGUAGE_CATALOG_TITLE_AVAILABILITY_SEVERITY, LANGUAGE_CATALOG_TITLE_AVAILABILITY_MESSAGE);
    }
}
