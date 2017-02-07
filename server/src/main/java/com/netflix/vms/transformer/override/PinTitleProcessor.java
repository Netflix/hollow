package com.netflix.vms.transformer.override;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import java.io.File;

public interface PinTitleProcessor {
    public static enum TYPE {
        INPUT, OUTPUT
    }

    public HollowReadStateEngine process(long dataVersion, int... topNodes) throws Throwable;

    public File getFile(TYPE type, long version, int... topNodes) throws Exception;

    public File process(TYPE type, long dataVersion, int... topNodes) throws Throwable;

    public String getVip();

    public void setPinTitleFileStore(FileStore pinTitleFileStore);
}