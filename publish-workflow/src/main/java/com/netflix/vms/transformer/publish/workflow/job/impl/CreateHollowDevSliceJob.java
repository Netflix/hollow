package com.netflix.vms.transformer.publish.workflow.job.impl;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.DEV_SLICE_TOPNODE_IDS;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CreateDevSlice;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.config.NetflixConfiguration.RegionEnum;
import com.netflix.hollow.util.IntList;
import com.netflix.hollow.write.HollowBlobWriter;
import com.netflix.hollow.write.HollowWriteStateEngine;
import com.netflix.videometadata.s3.HollowBlobKeybaseBuilder;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.slice.DataSlicer;
import com.netflix.vms.transformer.io.LZ4VMSOutputStream;
import com.netflix.vms.transformer.publish.workflow.HollowBlobDataProvider;
import com.netflix.vms.transformer.publish.workflow.HollowBlobFileNamer;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import com.netflix.vms.transformer.publish.workflow.job.AnnounceJob;
import com.netflix.vms.transformer.publish.workflow.job.CreateDevSliceJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class CreateHollowDevSliceJob extends CreateDevSliceJob {

    private final HollowBlobDataProvider dataProvider;
    private final DataSlicer dataSlicer;
    private final HermesVipAnnouncer announcer;
    private final String sliceVip;
    
    public CreateHollowDevSliceJob(PublishWorkflowContext ctx, AnnounceJob dependency, HollowBlobDataProvider dataProvider, DataSlicer dataSlicer, HermesVipAnnouncer announcer, long currentCycleId) {
        super(ctx, dependency, currentCycleId);
        this.dataProvider = dataProvider;
        this.dataSlicer = dataSlicer;
        this.announcer = announcer;
        this.sliceVip = ctx.getVip() + "_devslice";
    }

    @Override
    protected boolean executeJob() {
        try {
            HollowWriteStateEngine sliceOutputBlob = createSlice();
            
            File sliceSnapshotFile = writeSnapshotFile(sliceOutputBlob);

            publishSlice(sliceSnapshotFile, RegionEnum.US_EAST_1);
            publishSlice(sliceSnapshotFile, RegionEnum.US_WEST_2);
            publishSlice(sliceSnapshotFile, RegionEnum.EU_WEST_1);

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
        
        ///TODO: This is the only place where we require the vmstransformer-io project.  When this
        /// changes to an LZ4BlockOutputStream, remove the dependency on vmstransformer-io.
        try (OutputStream os = new LZ4VMSOutputStream(new FileOutputStream(sliceSnapshotFile))) {
            writer.writeSnapshot(os);
        }
        return sliceSnapshotFile;
    }

    private void publishSlice(File sliceSnapshotFile, RegionEnum region) throws Exception {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(sliceVip);
        ctx.getFileStore().publish(sliceSnapshotFile, keybaseBuilder.getSnapshotKeybase(), String.valueOf(getCycleVersion()), RegionEnum.US_EAST_1);
        announcer.announce(sliceVip, region, false, getCycleVersion());
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
