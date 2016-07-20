package com.netflix.vms.transformer.override;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowWriteStateEngine;

import java.io.File;

public class TitleOverrideHelper {
    public static final String BLOB_ID = "BLOB_ID";

    public static void addBlobID(HollowWriteStateEngine writeStateEngine, String blobID) {
        writeStateEngine.addHeaderTag(BLOB_ID, blobID);
    }

    public static void addBlobID(HollowWriteStateEngine writeStateEngine, File file) {
        addBlobID(writeStateEngine, file.getName());
    }

    public static void addBlobID(HollowWriteStateEngine writeStateEngine, HollowReadStateEngine... inputs) {
        StringBuilder blobID = new StringBuilder();
        for (HollowReadStateEngine i : inputs) {
            if (blobID.length() > 0) blobID.append(",");
            blobID.append(getBlobID(i));
        }

        addBlobID(writeStateEngine, blobID.toString());
    }

    public static String getBlobID(HollowReadStateEngine readStateEngine) {
        return readStateEngine.getHeaderTag(BLOB_ID);
    }


}
