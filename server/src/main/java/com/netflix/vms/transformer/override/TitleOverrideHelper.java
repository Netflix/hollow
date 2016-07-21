package com.netflix.vms.transformer.override;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.write.HollowWriteStateEngine;

import java.io.File;

public class TitleOverrideHelper {
    public static final String OVERRIDEBLOB_ID = "OVERRIDEBLOB_ID";

    public static String addBlobID(HollowWriteStateEngine writeStateEngine, String blobID) {
        writeStateEngine.addHeaderTag(OVERRIDEBLOB_ID, blobID);
        return blobID;
    }

    public static String addBlobID(HollowWriteStateEngine writeStateEngine, File file) {
        return addBlobID(writeStateEngine, file.getName());
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
        return readStateEngine.getHeaderTag(OVERRIDEBLOB_ID);
    }


}
