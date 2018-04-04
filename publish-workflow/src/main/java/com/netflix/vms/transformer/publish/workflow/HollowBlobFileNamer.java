package com.netflix.vms.transformer.publish.workflow;

import com.netflix.vms.transformer.publish.workflow.util.VipNameUtil;
import java.io.File;
import org.apache.commons.codec.digest.DigestUtils;

public class HollowBlobFileNamer {
    private static final String VMS_PREFIX = "vms.";
    private static final String FILENAME_PREFIX = System.getProperty("java.io.tmpdir") + File.separatorChar + VMS_PREFIX;
    private final String vip;

    public HollowBlobFileNamer(String vip){
        this.vip = vip;
    }

    public String getDeltaFileName(long previousVersion, long nextVersion) {
        return FILENAME_PREFIX + vip + "-delta-" + previousVersion + "-" + nextVersion;
    }

    public String getNostreamsDeltaFileName(long previousVersion, long nextVersion) {
        return FILENAME_PREFIX + VipNameUtil.getNoStreamsVip(vip) + "-delta-" + previousVersion + "-" + nextVersion;
    }

    public String getReverseDeltaFileName(long nextVersion, long previousVersion) {
        return FILENAME_PREFIX + vip + "-reversedelta-" + nextVersion + "-" + previousVersion;
    }

    public String getNostreamsReverseDeltaFileName(long nextVersion, long previousVersion) {
        return FILENAME_PREFIX + VipNameUtil.getNoStreamsVip(vip) + "-reversedelta-" + nextVersion + "-" + previousVersion;
    }

    public String getSnapshotFileName(long nextVersion) {
        return FILENAME_PREFIX + vip + "-snapshot-" + nextVersion;
    }

    public String getNostreamsSnapshotFileName(long nextVersion) {
        return FILENAME_PREFIX + VipNameUtil.getNoStreamsVip(vip) + "-snapshot-" + nextVersion;
    }

    public String getPinTitleFileName(long version, boolean includeFullPath, int ... topNodes) {
        String suffix = vip + "-pinnedtitle-" + version + "-";

        StringBuilder firstSetSB = new StringBuilder();
        StringBuilder restSb = new StringBuilder();
        for (int i = 0; i < topNodes.length; i++) {
            StringBuilder sb = i < 5 ? firstSetSB : restSb;

            if (i > 0) sb.append("_");
            sb.append(topNodes[i]);
        }

        suffix += firstSetSB.toString();
        if (restSb.length() > 0) { // Make sure the generated String does not exceed filename limit
            suffix += "_" + DigestUtils.md5Hex(restSb.toString());
        }

        if (includeFullPath) {
            return FILENAME_PREFIX + suffix;
        } else {
            return VMS_PREFIX + suffix;
        }
    }
}