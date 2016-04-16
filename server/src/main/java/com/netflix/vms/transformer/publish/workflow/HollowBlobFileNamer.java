package com.netflix.vms.transformer.publish.workflow;

import java.io.File;

public class HollowBlobFileNamer {
    private static final String FILENAME_PREFIX = System.getProperty("java.io.tmpdir") + File.separatorChar + "vms.";
    private final String vip;

    public HollowBlobFileNamer(String vip){
        this.vip = vip;
    }

    public String getDeltaFileName(long previousVersion, long nextVersion) {
        return FILENAME_PREFIX + vip + "-delta-" + previousVersion + "-" + nextVersion;
    }

    public String getReverseDeltaFileName(long nextVersion, long previousVersion) {
        return FILENAME_PREFIX + vip + "-reversedelta-" + nextVersion + "-" + previousVersion;
    }
    public String getSnapshotFileName(long nextVersion) {
        return FILENAME_PREFIX + vip + "-snapshot-" + nextVersion;
    }
}