package com.netflix.vms.transformer.publish.workflow.job.impl;

import com.netflix.aws.db.ItemAttribute;
import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.videometadata.s3.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.common.TransformerLogger.LogTag;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import netflix.admin.videometadata.uploadstat.FileUploadStatus.FileRegionUploadStatus;
import netflix.admin.videometadata.uploadstat.FileUploadStatus.UploadStatus;

public class FileStoreHollowBlobPublishJob extends HollowBlobPublishJob {

    private static final int  RETRY_ATTEMPTS = 10;
    
    public FileStoreHollowBlobPublishJob(PublishWorkflowContext ctx, long inputVersion, long previousVersion, long version, PublishType jobType, RegionEnum region, File fileToUpload) {
        super(ctx, ctx.getVip(), inputVersion, previousVersion, version, jobType, region, fileToUpload);
    }

    @Override
    protected boolean executeJob() {
        FileStore fileStore = ctx.getFileStore();
        String keybase = getKeybase();
        String currentVersion = String.valueOf(getCycleVersion());
        String fileStoreVersion = jobType == PublishType.DELTA ? String.valueOf(previousVersion) : currentVersion;

        long size = fileToUpload.length();
        RegionEnum region = this.region;

        FileRegionUploadStatus status = fileUploadStatus(currentVersion, keybase, size, region);

        boolean success = false;

        long startTime = System.currentTimeMillis();

        try {
            int retryCount = 0;
            while(retryCount < RETRY_ATTEMPTS) {
                try {
                    fileStore.publish(fileToUpload, keybase, fileStoreVersion, region, getItemAttributes());
                    fileStore.removeObsoleteVersions(keybase, region == RegionEnum.US_EAST_1 ? 4096 : 1024, region);
                    success=true;
                    break;
                } catch(Exception e) {
                    //status.setStatus(UploadStatus.RETRYING);
                    //status.incrementRetryCount();
                    e.printStackTrace();
                }
            }

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        logResult(keybase, status, success, startTime);

        return success;
    }

    private FileRegionUploadStatus fileUploadStatus(String currentVersion, String keybase, long size,
            RegionEnum region) {
        return ctx.serverUploadStatus().get().getCycle(currentVersion).getStatus(keybase, size).getUploadStatus(region);
    }

    private void logResult(String keybase, FileRegionUploadStatus status, boolean success, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        if(success) {
            status.setStatus(UploadStatus.SUCCESS);
            ctx.getLogger().info(LogTag.PublishedBlob, "Uploaded VMS Blob: keybase=" + keybase + "; region=" + status.getRegion() + " version=" +getCycleVersion() + " dataVersion=" + getCycleVersion() +" size=" + fileToUpload.length() + " duration=" +duration+ "ms");
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
        BlobMetaDataUtil.addPublisherProps(vip, att, publishedTimestamp, currentVersion, previousVersion);

        if(jobType == PublishType.REVERSEDELTA) {
            BlobMetaDataUtil.addAttribute(att, "fromVersion", currentVersion);
            BlobMetaDataUtil.addAttribute(att, "toVersion", previousVersion);
        } else {
            if(jobType == PublishType.DELTA)
                BlobMetaDataUtil.addAttribute(att, "fromVersion", previousVersion);
            BlobMetaDataUtil.addAttribute(att, "toVersion", currentVersion);
        }
        
        BlobMetaDataUtil.addAttribute(att, "converterVip", ctx.getConfig().getConverterVip());
        BlobMetaDataUtil.addAttribute(att, "inputVersion", String.valueOf(inputVersion));
        BlobMetaDataUtil.addAttribute(att, "publishCycleDataTS", String.valueOf(ctx.getNowMillis()));

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
