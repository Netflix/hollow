package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowClientUpdateDirector;

public class VMSInputDataUpdateDirector extends HollowClientUpdateDirector {

    private final FileStore fileStore;
    private final VMSInputDataKeybaseBuilder keybaseBuilder;

    public VMSInputDataUpdateDirector(FileStore fileStore, String converterVip) {
        this.fileStore = fileStore;
        this.keybaseBuilder = new VMSInputDataKeybaseBuilder(converterVip);
    }

    @Override
    public long getLatestVersion() {
        FileAccessItem snapshotItem = fileStore.getPublishedFileAccessItem(keybaseBuilder.getSnapshotKeybase());
        FileAccessItem deltaItem = fileStore.getPublishedFileAccessItem(keybaseBuilder.getDeltaKeybase());

        long snapshotVersion = snapshotItem == null ? Long.MIN_VALUE : FileStoreUtil.getToVersion(snapshotItem);
        long deltaVersion = deltaItem == null ? Long.MIN_VALUE : FileStoreUtil.getToVersion(deltaItem);

        return Math.max(snapshotVersion, deltaVersion);
    }

    @Override
    public void subscribeToEvents() { }

}
