package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.hollow.client.HollowTransitionCreator;
import com.netflix.hollow.client.HollowUpdateTransition;
import com.netflix.logging.ILog;
import com.netflix.logging.LogManager;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VMSInputDataTransitionCreator implements HollowTransitionCreator {

    private static final ILog LOGGER = LogManager.getLogger(VMSInputDataTransitionCreator.class);

    private final FileStore fileStore;
    private final String converterVip;

    public VMSInputDataTransitionCreator(FileStore fileStore, String converterVip) {
        this.fileStore = fileStore;
        this.converterVip = converterVip;
    }

    @Override
    public HollowUpdateTransition createSnapshotTransition(long desiredVersion) {
        TransitionResult result = new TransitionResult();
        String snapshotKeybase = getSnapshotKeybase();

        int retryCount = 0;
        while(retryCount < 3) {
            retryCount++;

            try {
                FileAccessItem latestItem = fileStore.getPublishedFileAccessItem(snapshotKeybase);
                if(latestItem != null) {
                    long version = FileStoreUtil.getToVersion(latestItem);

                    if(version <= desiredVersion)
                        return createTransition(latestItem);
                    else
                        break;
                }
            } catch(Exception e) { }

        }

        if(result.transition != null)
            return result.transition;

        retryCount = 0;
        while(retryCount < 3) {
            retryCount++;
            try {
                List<FileAccessItem> allFileAccessItems = fileStore.getAllFileAccessItems(snapshotKeybase);

                Collections.sort(allFileAccessItems, new Comparator<FileAccessItem>() {
                    public int compare(FileAccessItem o1, FileAccessItem o2) {
                        long toVersion1 = FileStoreUtil.getToVersion(o1);
                        long toVersion2 = FileStoreUtil.getToVersion(o2);

                        if(toVersion1 < toVersion2)
                            return 1;
                        if(toVersion1 > toVersion2)
                            return -1;
                        return 0;
                    }
                });

                for(FileAccessItem item : allFileAccessItems) {
                    long toVersion = FileStoreUtil.getToVersion(item);
                    if(toVersion <= desiredVersion) {
                        return createTransition(item);
                    }
                }

                break;

            } catch(Exception e) {
                LOGGER.error(e);
            }
        }

        return null;
    }

    @Override
    public HollowUpdateTransition createDeltaTransition(long currentVersion) {
        int retryCount = 0;
        while(retryCount < 3) {
            retryCount++;
            try {
                FileAccessItem fileAccessItem = fileStore.getPublishedFileAccessItem(getDeltaKeybase(), String.valueOf(currentVersion));
                if(fileAccessItem == null)
                    return null;

                return createTransition(fileAccessItem);
            } catch(Exception e) {
                LOGGER.error(e);
            }
        }

        return null;
    }

    @Override
    public HollowUpdateTransition createReverseDeltaTransition(long currentVersion) {
        return null;
    }

    private FileStoreHollowUpdateTransition createTransition(FileAccessItem latestItem) {
        FileStoreHollowUpdateTransition transition = new FileStoreHollowUpdateTransition(latestItem, fileStore);
        return transition;
    }

    private class TransitionResult {
        HollowUpdateTransition transition = null;
    }

    private String getSnapshotKeybase() {
        return "vms.hollowinput.blob." + converterVip + ".snapshot";
    }

    private String getDeltaKeybase() {
        return "vms.hollowinput.blob." + converterVip + ".delta";
    }

}
