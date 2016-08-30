package com.netflix.vms.transformer.override;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowWriteStateEngine;

public class PinTitleHelper {
    public static final String BLOB_ID = "BLOB_ID";

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
}
