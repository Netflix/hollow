package com.netflix.hollow.diff.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.report.HollowDiffReportBuilder;
import com.netflix.hollow.tools.diff.report.HollowDiffReportMetadata;
import com.netflix.hollow.tools.diff.report.HollowDiffReportOptions;

final class HollowDiffJsonSerializer {

    private static final Gson GSON = new GsonBuilder().create();

    private HollowDiffJsonSerializer() {}

    static String toJson(
            HollowDiff diff, HollowDiffReportMetadata metadata, HollowDiffReportOptions options) {
        return GSON.toJson(HollowDiffReportBuilder.build(metadata, diff, options));
    }
}
