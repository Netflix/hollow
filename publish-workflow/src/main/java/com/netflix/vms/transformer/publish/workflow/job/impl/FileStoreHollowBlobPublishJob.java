package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.PublishedBlob;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.CONVERTER;
import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier.GATEKEEPER2;

import com.netflix.aws.file.FileStore;
import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.Blob;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.vms.transformer.common.input.CycleInputs;
import com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.UpstreamDatasetConfig;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.HollowBlobPublishJob;
import com.netflix.vms.transformer.publish.workflow.logmessage.PublishBlobMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import netflix.admin.videometadata.uploadstat.FileUploadStatus.FileRegionUploadStatus;
import netflix.admin.videometadata.uploadstat.FileUploadStatus.UploadStatus;

public class FileStoreHollowBlobPublishJob extends HollowBlobPublishJob {

    private static final int  RETRY_ATTEMPTS = 10;
    
    public FileStoreHollowBlobPublishJob(PublishWorkflowContext ctx, String vip,
            CycleInputs cycleInputs,
            long previousVersion, long version,
            PublishType jobType, File fileToUpload, boolean isNostreams) {
        super(ctx, vip, cycleInputs, previousVersion, version,
                jobType, fileToUpload, isNostreams);
    }

    @Override
    public boolean executeJob() {
        return executeJob(true, 0);
    }

    public boolean executeJob(boolean publish, long duration) {
        FileStore fileStore = ctx.getFileStore();
        HollowProducer.Publisher publisher = isNostreams ? ctx.getNostreamsBlobPublisher() : ctx.getBlobPublisher();
        String currentVersion = String.valueOf(getCycleVersion());
        String filestoreKeybase = getKeybase();
        String fileStoreVersion = jobType == PublishType.DELTA ? String.valueOf(previousVersion) : currentVersion;
        String filestoreVersionKey = getKeybase() + "." + fileStoreVersion;

        long size = fileToUpload.length();

        FileRegionUploadStatus status = fileUploadStatus(currentVersion, filestoreKeybase, size, RegionEnum.US_EAST_1);

        boolean success = false;

        long startTime = System.currentTimeMillis();

        try {
            while(status.getRetryCount() < RETRY_ATTEMPTS) {
                try {
                    if (publish) {
                        publisher.publish(fakeProducerBlob(fileToUpload));
                    }

                    String environmentBucketPostfix = "prod".equals(NetflixConfiguration.getEnvironment()) ? "prod" : "test";

                    fileStore.writeMetadata("netflix.bulkdata." + environmentBucketPostfix,
                            getGutenbergS3ObjectName(), filestoreKeybase, filestoreVersionKey, System.currentTimeMillis(), RegionEnum.US_EAST_1, getItemAttributes());
                    fileStore.writeMetadata("us-west-2.netflix.bulkdata." + environmentBucketPostfix, getGutenbergS3ObjectName(), filestoreKeybase, filestoreVersionKey, System.currentTimeMillis(), RegionEnum.US_WEST_2, getItemAttributes());
                    fileStore.writeMetadata("eu-west-1.netflix.bulkdata." + environmentBucketPostfix, getGutenbergS3ObjectName(), filestoreKeybase, filestoreVersionKey, System.currentTimeMillis(), RegionEnum.EU_WEST_1, getItemAttributes());
                    fileStore.setCurrentVersion(filestoreKeybase, filestoreVersionKey, RegionEnum.US_EAST_1, getItemAttributes());
                    fileStore.setCurrentVersion(filestoreKeybase, filestoreVersionKey, RegionEnum.US_WEST_2, getItemAttributes());
                    fileStore.setCurrentVersion(filestoreKeybase, filestoreVersionKey, RegionEnum.EU_WEST_1, getItemAttributes());

                    fileStore.removeObsoleteVersions(filestoreKeybase, 16384, RegionEnum.US_EAST_1);
                    fileStore.removeObsoleteVersions(filestoreKeybase, 16384, RegionEnum.US_WEST_2);
                    fileStore.removeObsoleteVersions(filestoreKeybase, 16384, RegionEnum.EU_WEST_1);
                    success=true;
                    break;
                } catch(Exception e) {
                    status.setStatus(UploadStatus.RETRYING);
                    status.incrementRetryCount();
                    e.printStackTrace();
                }
            }

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        logResult(filestoreKeybase, status, success, duration + (System.currentTimeMillis() - startTime));

        return success;
    }

    private FileRegionUploadStatus fileUploadStatus(String currentVersion, String keybase, long size,
            RegionEnum region) {
        return ctx.serverUploadStatus().get().getCycle(currentVersion).getStatus(keybase, size).getUploadStatus(region);
    }
    
    private String getGutenbergS3ObjectName() {
        switch(jobType) {
        case SNAPSHOT:
            return "gutenberg/hollow.vms-" + vip + ".snapshot/" + getCycleVersion();
        case DELTA:
            return "gutenberg/hollow.vms-" + vip + ".delta/" + previousVersion;
        case REVERSEDELTA:
            return "gutenberg/hollow.vms-" + vip + ".reversedelta/" + getCycleVersion();
        }
        
        throw new IllegalStateException();
    }

    private void logResult(String keybase, FileRegionUploadStatus status, boolean success, long duration) {
        if(success) {
            status.setStatus(UploadStatus.SUCCESS);
            ctx.getLogger().info(PublishedBlob, new PublishBlobMessage(keybase, status.getRegion(), getCycleVersion(), getCycleVersion(), fileToUpload.length(), duration));
        } else {
            status.setStatus(UploadStatus.FAILED);
        }
    }

    @SuppressWarnings("deprecation")
    private List<com.netflix.aws.db.ItemAttribute> getItemAttributes() {
        List<com.netflix.aws.db.ItemAttribute> att = new ArrayList<>(4);

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

        // for backwards compatibility, version attributes with the 'old' names continue to be written
        // these can be dropped by the end of Q3 2019
        BlobMetaDataUtil.addAttribute(att, "inputVersion", String.valueOf(
                cycleInputs.getInputs().get(CONVERTER).getVersion()));
        BlobMetaDataUtil.addAttribute(att, "gk2InputVersion", String.valueOf(
                cycleInputs.getInputs().get(GATEKEEPER2) == null ? Long.MIN_VALUE
                        : cycleInputs.getInputs().get(GATEKEEPER2).getVersion()));

        // Add input version attributes for all inputs
        cycleInputs.getInputs().forEach((k, v) ->
                BlobMetaDataUtil.addAttribute(att, UpstreamDatasetConfig.getInputVersionAttribute(k), String.valueOf(v.getVersion())));

        BlobMetaDataUtil.addAttribute(att, "publishCycleDataTS", String.valueOf(ctx.getNowMillis()));

        return att;
    }

    private Blob fakeProducerBlob(File file) {
        Blob.Type blobType = null;
        long fromVersion;
        long toVersion;
        
        switch(jobType) {
        case SNAPSHOT:
            blobType = Blob.Type.SNAPSHOT;
            fromVersion = Long.MIN_VALUE;
            toVersion = getCycleVersion();
            break;
        case DELTA:
            blobType = Blob.Type.DELTA;
            fromVersion = previousVersion;
            toVersion = getCycleVersion();
            break;
        case REVERSEDELTA:
            blobType = Blob.Type.REVERSE_DELTA;
            fromVersion = getCycleVersion();
            toVersion = previousVersion;
            break;
        default:
            throw new IllegalStateException("Publish job type undefined!");
        }
        
         
                
        
        Blob blob = new Blob(fromVersion, toVersion, blobType) {
            @Override public File getFile() {
                return file;
            }

            @Override protected void write(HollowBlobWriter writer) throws IOException { throw new UnsupportedOperationException(); }
            @Override public InputStream newInputStream() throws IOException { throw new UnsupportedOperationException(); }
            @Override public void cleanup() { throw new UnsupportedOperationException(); }
            
        };
        return blob;
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
