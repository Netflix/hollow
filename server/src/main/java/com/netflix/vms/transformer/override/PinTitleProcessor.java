package com.netflix.vms.transformer.override;

import com.netflix.config.NetflixConfiguration;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.io.File;

public interface PinTitleProcessor {
    public static enum TYPE {
        INPUT, OUTPUT
    }

    public static final String PIN_TITLE_S3_BUCKET = "netflix.bulkdata." + ("prod".equals(NetflixConfiguration.getEnvironment()) ? "prod" : "test");
    public static final String PIN_TITLE_S3_PATH = "pin_title_cache";
    public static final String PIN_TITLE_S3_REGION = NetflixConfiguration.RegionEnum.US_EAST_1.key();

    public HollowReadStateEngine process(long dataVersion, int... topNodes) throws Throwable;

    public File getFile(String namespace, TYPE type, long version, int... topNodes) throws Exception;

    public File process(TYPE type, long dataVersion, int... topNodes) throws Throwable;
}