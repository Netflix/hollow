package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.client.HollowAnnouncementWatcher;

public class VMSInputDataUpdateDirector extends HollowAnnouncementWatcher {

    private final FileStore fileStore;
    private final VMSInputDataKeybaseBuilder keybaseBuilder;
    private Long latestVersion = null;

    public VMSInputDataUpdateDirector(FileStore fileStore, String converterVip) {
        this.fileStore = fileStore;
        this.keybaseBuilder = new VMSInputDataKeybaseBuilder(converterVip);
    }

    @Override
    public long getLatestVersion() {
        // TODO: timt: outcast, unclean!
        if (latestVersion == null) {
            FileAccessItem snapshotItem = fileStore.getPublishedFileAccessItem(keybaseBuilder.getSnapshotKeybase());
            FileAccessItem deltaItem = fileStore.getPublishedFileAccessItem(keybaseBuilder.getDeltaKeybase());

            long snapshotVersion = snapshotItem == null ? Long.MIN_VALUE : FileStoreUtil.getToVersion(snapshotItem);
            long deltaVersion = deltaItem == null ? Long.MIN_VALUE : FileStoreUtil.getToVersion(deltaItem);

            return Math.max(snapshotVersion, deltaVersion);
        } else {
            Long temp = latestVersion;
            latestVersion = null;
            return temp;
        }
    }

    @Override
    public void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }

    @Override
    public void subscribeToEvents() { }

}
