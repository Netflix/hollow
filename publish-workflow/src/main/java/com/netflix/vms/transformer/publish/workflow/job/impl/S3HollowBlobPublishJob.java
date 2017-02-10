package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PublishedBlob;

import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobPublisher;
import com.netflix.vms.transformer.common.io.TransformerLogTag;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.logmessage.PublishBlobMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import netflix.admin.videometadata.uploadstat.FileUploadStatus.FileRegionUploadStatus;
import netflix.admin.videometadata.uploadstat.FileUploadStatus.UploadStatus;

public class S3HollowBlobPublishJob extends HollowBlobPublishJob {

    private final boolean isNoStreams;
    
    public S3HollowBlobPublishJob(PublishWorkflowContext ctx, long inputVersion, long previousVersion, long version, PublishType jobType, File fileToUpload, boolean isNoStreams) {
        super(ctx, ctx.getVip(), inputVersion, previousVersion, version, jobType, fileToUpload);
        this.isNoStreams = isNoStreams;
    }

    @Override
    protected boolean executeJob() {
        NetflixS3BlobPublisher blobPublisher = isNoStreams ? ctx.getNostreamsBlobPublisher() : ctx.getBlobPublisher();
        
        ///TODO: No regional status available.
        FileRegionUploadStatus status = fileUploadStatus(getCycleVersion(), fileToUpload.length(), RegionEnum.US_EAST_1);
        boolean success = false;
        long startTime = System.currentTimeMillis();
        
        try {
            switch(getPublishType()) {
            case SNAPSHOT:
                blobPublisher.publishSnapshot(fileToUpload, getCycleVersion(), getBlobMetadata());
                break;
            case DELTA:
                blobPublisher.publishDelta(fileToUpload, previousVersion, getCycleVersion(), getBlobMetadata());
                break;
            case REVERSEDELTA:
                blobPublisher.publishReverseDelta(fileToUpload, previousVersion, getCycleVersion(), getBlobMetadata());
                break;
            }
            
            success = true;
        } catch(Throwable th) {
            ctx.getLogger().error(TransformerLogTag.PublishedBlob, "Failed to publish", th);
        }
        
        logResult(isNoStreams, status, success, startTime);
        return success;
    }

    private FileRegionUploadStatus fileUploadStatus(long currentVersion, long size, RegionEnum region) {
        return ctx.serverUploadStatus().get().getCycle(String.valueOf(currentVersion)).getStatus(getPublishType().toString(), size).getUploadStatus(region);
    }

    private void logResult(boolean isNostreams, FileRegionUploadStatus status, boolean success, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        if(success) {
            status.setStatus(UploadStatus.SUCCESS);
            ctx.getLogger().info(PublishedBlob, new PublishBlobMessage(vip, isNostreams, getCycleVersion(), getCycleVersion(), fileToUpload.length(), duration));
        } else {
            status.setStatus(UploadStatus.FAILED);
        }
    }

    private Map<String, String> getBlobMetadata() {
        Map<String, String> attributes = new HashMap<String, String>(
                BlobMetaDataUtil.getPublisherProps(vip, System.currentTimeMillis(), String.valueOf(getCycleVersion()), String.valueOf(previousVersion))
        );
        
        attributes.put("converterVip", ctx.getConfig().getConverterVip());
        attributes.put("inputVersion", String.valueOf(inputVersion));
        attributes.put("publishCycleDataTS", String.valueOf(ctx.getNowMillis()));

        return attributes;
    }
}
