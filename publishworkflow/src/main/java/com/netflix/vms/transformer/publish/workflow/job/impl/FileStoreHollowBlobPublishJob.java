package com.netflix.vms.transformer.publish.workflow.job.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.videometadata.s3.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;

import netflix.admin.videometadata.uploadstat.VMSFileUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSFileUploadStatus.UploadStatus;
import netflix.admin.videometadata.uploadstat.VMSFileUploadStatus.VMSFileRegionUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSServerCycleUploadStatus;
import netflix.admin.videometadata.uploadstat.VMSServerUploadStatus;

public class FileStoreHollowBlobPublishJob extends HollowBlobPublishJob {

    private static final int  RETRY_ATTEMPTS = 10;
    private static final long TIMEOUT_MILLIS = 60 * 60 * 1000; /// 1 hour

    public FileStoreHollowBlobPublishJob(PublishWorkflowContext ctx, long previousVersion, long version, PublishType jobType, RegionEnum region, File fileToUpload) {
        super(ctx, ctx.getVip(), previousVersion, version, jobType, region, fileToUpload);
    }

    @Override
    protected boolean executeJob() {
        FileStore fileStore = ctx.getFileStore();
        String keybase = getKeybase();
        String fileStoreVersion = jobType == PublishType.DELTA ? String.valueOf(previousVersion) : String.valueOf(getCycleVersion());

        VMSServerCycleUploadStatus cycleStatus = VMSServerUploadStatus.get().getCycle(String.valueOf(getCycleVersion()));
        VMSFileUploadStatus fileStatus = cycleStatus.getStatus(keybase, fileToUpload.length());
        VMSFileRegionUploadStatus status = fileStatus.getUploadStatus(region);

        boolean success = false;

        long startTime = System.currentTimeMillis();

        try {
            int retryCount = 0;
            while(retryCount < 3) {
                try {
                    fileStore.publish(fileToUpload, keybase, fileStoreVersion, region, getItemAttributes());
                    fileStore.removeObsoleteVersions(keybase, region == RegionEnum.US_EAST_1 ? 4096 : 1024, region);
                    success=true;
                } catch(Exception e) {
                    //status.setStatus(UploadStatus.RETRYING);
                    //status.incrementRetryCount();
                    throw e;
                }
            }

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        logResult(keybase, status, success, startTime);

        return success;
    }

    private void logResult(String keybase, VMSFileRegionUploadStatus status, boolean success, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        if(success) {
            status.setStatus(UploadStatus.SUCCESS);
            ctx.getLogger().info("PublishedBlob", "Uploaded VMS Blob: keybase=" + keybase + "; region=" + status.getRegion() + " version=" +getCycleVersion() + " dataVersion=" + getCycleVersion() +" size=" + fileToUpload.length() + " duration=" +duration+ "ms");
        } else {
            status.setStatus(UploadStatus.FAILED);
        }
    }

    @SuppressWarnings("deprecation")
    private List<ItemAttribute> getItemAttributes() {
        List<ItemAttribute> att = new ArrayList<ItemAttribute>(4);

        String previousVersion = this.previousVersion == Long.MIN_VALUE ? "" : String.valueOf(this.previousVersion);
        String currentVersion =  String.valueOf(getCycleVersion());

        long publishedTimestamp = System.currentTimeMillis();
        BlobMetaDataUtil.addPublisherProps(att, publishedTimestamp, currentVersion, previousVersion);

        if(jobType == PublishType.REVERSEDELTA) {
            BlobMetaDataUtil.addAttribute(att, "fromVersion", currentVersion);
            BlobMetaDataUtil.addAttribute(att, "toVersion", previousVersion);
        } else {
            if(jobType == PublishType.DELTA)
                BlobMetaDataUtil.addAttribute(att, "fromVersion", previousVersion);
            BlobMetaDataUtil.addAttribute(att, "toVersion", currentVersion);
        }

        return att;
    }


    private String getKeybase() {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(vip);

        switch(jobType) {
            case SNAPSHOT:
                return keybaseBuilder.getSnapshotKeybase();
            case DELTA:
                return keybaseBuilder.getDeltaKeybase();
            case REVERSEDELTA:
                return keybaseBuilder.getReverseDeltaKeybase();
        }

        throw new RuntimeException("No keybase specified for " + jobType);
    }

}
