package com.netflix.vms.transformer.override;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowWriteStateEngine;

public class PinTitleHelper {
    public static final String BLOB_ID = "BLOB_ID";
    public static final String PINNED_TITLES = "PINNED_TITLES";

    public static String createBlobID(String prefix, long version, int... topNodes) {
        return prefix + ":" + version + ":" + toString("_", topNodes);
    }

    public static String addBlobID(HollowWriteStateEngine writeStateEngine, String blobID) {
        writeStateEngine.addHeaderTag(BLOB_ID, blobID);
        return blobID;
    }

    public static String addBlobID(HollowWriteStateEngine writeStateEngine, HollowReadStateEngine... inputs) {
        StringBuilder blobID = new StringBuilder();
        for (HollowReadStateEngine i : inputs) {
            if (blobID.length() > 0) blobID.append(",");
            blobID.append(getBlobID(i));
        }

        return addBlobID(writeStateEngine, blobID.toString());
    }

    public static String getBlobID(HollowReadStateEngine readStateEngine) {
        return readStateEngine.getHeaderTag(BLOB_ID);
    }

    public static String getBlobID(HollowWriteStateEngine writeStateEngine) {
        return writeStateEngine.getHeaderTag(BLOB_ID);
    }

    public static String createPinnedTitlesInfo(long version, int... topNodes) {
        return version + ":" + toString(",", topNodes);
    }

    public static String addPinnedTitles(HollowWriteStateEngine writeStateEngine, long version, int... topNodes) {
        String value = createPinnedTitlesInfo(version, topNodes);
        writeStateEngine.addHeaderTag(PINNED_TITLES, value);
        return value;
    }

    public static String addPinnedTitles(HollowWriteStateEngine writeStateEngine, HollowReadStateEngine... inputs) {
        StringBuilder pinnedTitle = new StringBuilder();
        for (HollowReadStateEngine i : inputs) {
            String value = getPinnedTitles(i);
            if (value == null) continue;

            if (pinnedTitle.length() > 0) pinnedTitle.append("; ");
            pinnedTitle.append(value);
        }

        String value = pinnedTitle.toString();
        writeStateEngine.addHeaderTag(PINNED_TITLES, value);
        return value;
    }

    public static String getPinnedTitles(HollowReadStateEngine readStateEngine) {
        return readStateEngine.getHeaderTag(PINNED_TITLES);
    }

    public static String getPinnedTitles(HollowWriteStateEngine writeStateEngine) {
        return writeStateEngine.getHeaderTag(PINNED_TITLES);
    }

    public static String combineHeader(HollowWriteStateEngine writeStateEngine, HollowReadStateEngine... inputs) {
        addPinnedTitles(writeStateEngine, inputs);
        return addBlobID(writeStateEngine, inputs);
    }

    public static final String toString(int... topNodes) {
        return toString(",", topNodes);
    }

    public static final String toString(String delim, int... topNodes) {
        if (topNodes == null || topNodes.length == 0) return "";

        if (topNodes.length == 1) return String.valueOf(topNodes[0]);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < topNodes.length; i++) {
            if (i > 0) sb.append(delim);
            sb.append(topNodes[i]);
        }
        return sb.toString();
    }

    public static int[] parseTopNodes(String topNodes) {
        if (topNodes == null) return null;

        String[] parts = topNodes.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }
}
