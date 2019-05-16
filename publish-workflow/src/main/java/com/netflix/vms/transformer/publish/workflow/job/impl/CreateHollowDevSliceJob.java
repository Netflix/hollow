package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.DEV_SLICE_TOPNODE_IDS;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CreateDevSlice;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.aws.db.ItemAttribute;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CreateDevSliceJob;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.jpountz.lz4.LZ4BlockOutputStream;

@SuppressWarnings("deprecation")
public class CreateHollowDevSliceJob extends CreateDevSliceJob {

    private final HollowBlobDataProvider dataProvider;
    private final DataSlicer dataSlicer;
    private final String sliceVip;
    private final long inputVersion;
    private final HollowProducer.Publisher publisher;
    private final HollowProducer.Announcer announcer;
    
    public CreateHollowDevSliceJob(PublishWorkflowContext ctx, AnnounceJob dependency, HollowBlobDataProvider dataProvider, DataSlicer dataSlicer, long inputVersion, long currentCycleId) {
        super(ctx, dependency, currentCycleId);
        this.dataProvider = dataProvider;
        this.dataSlicer = dataSlicer;
        this.inputVersion = inputVersion;
        this.sliceVip = HermesTopicProvider.getDevSliceTopic(ctx.getVip());
        this.publisher = ctx.getDevSlicePublisher();
        this.announcer = ctx.getDevSliceAnnouncer();
    }

    @Override public boolean executeJob() {
        try {
            HollowWriteStateEngine sliceOutputBlob = createSlice();
            
            File sliceSnapshotFile = writeSnapshotFile(sliceOutputBlob);

            publishSlice(sliceSnapshotFile, RegionEnum.US_EAST_1);
            publishSlice(sliceSnapshotFile, RegionEnum.US_WEST_2);
            publishSlice(sliceSnapshotFile, RegionEnum.EU_WEST_1);

            // using HollowProducer.Publisher and Announcer, backed up by cinder publisher/announcer impls.
            publish(sliceSnapshotFile);
            announcer.announce(getCycleVersion());

            sliceSnapshotFile.delete();

            return true;
        } catch(Exception e){
            ctx.getLogger().error(CreateDevSlice, "Failed creating dev slice", e);
            throw new RuntimeException(e);
        }
    }

    private HollowWriteStateEngine createSlice() throws ConnectionException {
        DataSlicer.SliceTask sliceTask = dataSlicer.getSliceTask(0, getTopNodeIdsToInclude());
        HollowWriteStateEngine sliceOutputBlob = sliceTask.sliceOutputBlob(dataProvider.getStateEngine());
        return sliceOutputBlob;
    }

    private File writeSnapshotFile(HollowWriteStateEngine sliceOutputBlob) throws IOException, FileNotFoundException {
        HollowBlobFileNamer namer = new HollowBlobFileNamer(sliceVip);
        File sliceSnapshotFile = new File(namer.getSnapshotFileName(getCycleVersion()));
        
        HollowBlobWriter writer = new HollowBlobWriter(sliceOutputBlob);
        
        try (OutputStream os = new LZ4BlockOutputStream(new FileOutputStream(sliceSnapshotFile))) {
            writer.writeSnapshot(os);
        }
        return sliceSnapshotFile;
    }

    private void publishSlice(File sliceSnapshotFile, RegionEnum region) throws Exception {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(sliceVip);
        ctx.getFileStore().publish(sliceSnapshotFile, keybaseBuilder.getSnapshotKeybase(), String.valueOf(getCycleVersion()), region, getItemAttributes());
        ctx.getVipAnnouncer().announce(sliceVip, region, false, getCycleVersion());
    }

    // uploads the sliced snapshot file using HollowProducer.Publisher.
    private void publish(File sliceSnapshotFile) {
        // using Long.MIN_VALUE as fromVersion, since dev slice only supports SNAPSHOT mode
        publisher.publish(new HollowProducer.Blob(Long.MIN_VALUE, getCycleVersion(), HollowProducer.Blob.Type.SNAPSHOT) {
            @Override
            protected void write(HollowBlobWriter writer) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public InputStream newInputStream() throws IOException {
                return new BufferedInputStream(new FileInputStream(sliceSnapshotFile));
            }

            @Override
            public void cleanup() {
                // nothing to do, assuming clean up is done later
            }

            @Override
            public File getFile() {
                return sliceSnapshotFile;
            }

            @Override
            public Path getPath() {
                return sliceSnapshotFile.toPath();
            }
        });
    }
    
    private List<ItemAttribute> getItemAttributes() {
        List<ItemAttribute> att = new ArrayList<>(4);

        String currentVersion =  String.valueOf(getCycleVersion());

        long publishedTimestamp = System.currentTimeMillis();
        BlobMetaDataUtil.addPublisherProps(sliceVip, att, publishedTimestamp, currentVersion, "");

        BlobMetaDataUtil.addAttribute(att, "toVersion", String.valueOf(getCycleVersion()));
        
        BlobMetaDataUtil.addAttribute(att, "converterVip", ctx.getConfig().getConverterVip());
        BlobMetaDataUtil.addAttribute(att, "inputVersion", String.valueOf(inputVersion));
        BlobMetaDataUtil.addAttribute(att, "publishCycleDataTS", String.valueOf(ctx.getNowMillis()));

        return att;
    }
    
    private int[] getTopNodeIdsToInclude() throws ConnectionException {
        TransformerCassandraColumnFamilyHelper cassandraHelper = ctx.getCassandraHelper().getColumnFamilyHelper(DEV_SLICE_TOPNODE_IDS);
        
        Map<String, String> columns = cassandraHelper.getColumns("ids_0");
        
        IntList list = new IntList();
        
        for(Map.Entry<String, String>entry : columns.entrySet()) {
            try {
                list.add(Integer.parseInt(entry.getKey()));
            } catch(NumberFormatException nfe) {
                ctx.getLogger().error(CreateDevSlice, "Unable to parse top node ID: {}", entry.getKey());
            }
        }
        
        return list.arrayCopyOfRange(0, list.size());
    }
    
}
