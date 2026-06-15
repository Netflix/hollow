package com.netflix.hollow.tools.diff.report;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.HollowDiff;
import com.netflix.hollow.tools.diff.HollowTypeDiff;
import com.netflix.hollow.tools.diff.count.HollowFieldDiff;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builds a {@link HollowDiffReport} from a calculated {@link HollowDiff}.
 */
public final class HollowDiffReportBuilder {

    private HollowDiffReportBuilder() {}

    public static HollowDiffReport build(
            HollowDiffReportMetadata metadata,
            HollowDiff diff,
            HollowDiffReportOptions options) {
        HollowDiffReportMetadata meta =
                metadata != null ? metadata : HollowDiffReportMetadata.empty();
        HollowDiffReportOptions opts =
                options != null ? options : HollowDiffReportOptions.defaults();

        HollowDiffReport out = new HollowDiffReport();
        out.setBlobHeaders(blobHeaders(diff));
        if (meta.getFromNamespace() != null) {
            out.setFromNamespace(meta.getFromNamespace());
        }
        if (meta.getToNamespace() != null) {
            out.setToNamespace(meta.getToNamespace());
        }
        if (meta.getFromVersion() != null) {
            out.setFromVersion(meta.getFromVersion());
        }
        if (meta.getToVersion() != null) {
            out.setToVersion(meta.getToVersion());
        }

        List<?> typeDiffsRaw = diff.getTypeDiffs();
        List<HollowDiffReport.TypeDiff> rows = new ArrayList<>(typeDiffsRaw.size());
        for (Object tdObj : typeDiffsRaw) {
            HollowTypeDiff td = (HollowTypeDiff) tdObj;
            HollowDiffReport.TypeDiff tj = new HollowDiffReport.TypeDiff();
            tj.setType(td.getTypeName());
            tj.setTotalMatches(td.getTotalNumberOfMatches());
            tj.setTotalItemsInFromState(td.getTotalItemsInFromState());
            tj.setTotalItemsInToState(td.getTotalItemsInToState());

            IntList uFrom = td.getUnmatchedOrdinalsInFrom();
            IntList uTo = td.getUnmatchedOrdinalsInTo();
            tj.setUnmatchedOrdinalsInFromCount(uFrom.size());
            tj.setUnmatchedOrdinalsInToCount(uTo.size());
            tj.setUnmatchedOrdinalsInFromSample(
                    intListSample(uFrom, opts.getMaxUnmatchedSample()));
            tj.setUnmatchedOrdinalsInToSample(intListSample(uTo, opts.getMaxUnmatchedSample()));

            tj.setTypeDiffScore(td.getTotalDiffScore());

            List<?> fieldDiffs = td.getFieldDiffs();
            List<HollowDiffReport.FieldDiff> fjList = new ArrayList<>();
            if (fieldDiffs != null) {
                for (Object o : fieldDiffs) {
                    HollowFieldDiff fd = (HollowFieldDiff) o;
                    HollowDiffReport.FieldDiff fj = new HollowDiffReport.FieldDiff();
                    fj.setFieldPath(fd.getFieldIdentifier().toString());
                    fj.setTotalDiffScore(fd.getTotalDiffScore());
                    fj.setNumDiffs(fd.getNumDiffs());
                    fj.setPairsSample(fieldDiffPairsSample(fd, opts.getMaxFieldDiffPairsPerField()));
                    fjList.add(fj);
                }
            }
            tj.setFieldDiffs(fjList);
            rows.add(tj);
        }
        out.setTypeDiffs(rows);
        return out;
    }

    static List<Integer> intListSample(IntList list, int max) {
        if (list == null || list.size() == 0 || max <= 0) {
            return Collections.emptyList();
        }
        int n = Math.min(list.size(), max);
        List<Integer> sample = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            sample.add(list.get(i));
        }
        return sample;
    }

    static List<HollowDiffReport.FieldDiffPair> fieldDiffPairsSample(
            HollowFieldDiff fd, int maxPairs) {
        int num = fd.getNumDiffs();
        if (num <= 0 || maxPairs <= 0) {
            return Collections.emptyList();
        }
        int n = Math.min(num, maxPairs);
        List<HollowDiffReport.FieldDiffPair> pairs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            HollowDiffReport.FieldDiffPair p = new HollowDiffReport.FieldDiffPair();
            p.setFromOrdinal(fd.getFromOrdinal(i));
            p.setToOrdinal(fd.getToOrdinal(i));
            p.setScore(fd.getPairScore(i));
            pairs.add(p);
        }
        return pairs;
    }

    static List<HollowDiffReport.BlobHeader> blobHeaders(HollowDiff diff) {
        HollowReadStateEngine from = diff.getFromStateEngine();
        HollowReadStateEngine to = diff.getToStateEngine();
        if (from == null && to == null) {
            return Collections.emptyList();
        }
        Map<String, String> fromTags = from != null ? from.getHeaderTags() : Collections.emptyMap();
        Map<String, String> toTags = to != null ? to.getHeaderTags() : Collections.emptyMap();

        Set<String> allKeys = new TreeSet<>();
        allKeys.addAll(fromTags.keySet());
        allKeys.addAll(toTags.keySet());

        List<HollowDiffReport.BlobHeader> entries = new ArrayList<>(allKeys.size());
        for (String key : allKeys) {
            HollowDiffReport.BlobHeader entry = new HollowDiffReport.BlobHeader();
            entry.setHeaderName(key);
            entry.setFromValue(fromTags.get(key));
            entry.setToValue(toTags.get(key));
            entries.add(entry);
        }
        return entries;
    }
}
