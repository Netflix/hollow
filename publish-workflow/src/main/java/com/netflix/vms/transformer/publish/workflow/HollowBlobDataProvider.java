package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.BlobChecksum;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.BlobState;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CircuitBreaker;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.RollbackStateEngine;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.vms.logging.TaggingLogger.LogTag;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.job.impl.BlobMetaDataUtil;
import com.netflix.vms.transformer.publish.workflow.util.FileStatLogger;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class HollowBlobDataProvider {
    /* dependencies */
    private final TransformerContext ctx;

    /* fields */
    private HollowReadStateEngine hollowReadStateEngine;
    private HollowReadStateEngine nostreamsStateEngine;

    private HollowReadStateEngine revertableStateEngine;
    private HollowReadStateEngine revertableNostreamsStateEngine;

    public HollowBlobDataProvider(TransformerContext ctx) {
        this.ctx = ctx;
        this.hollowReadStateEngine = null;
        this.nostreamsStateEngine = null;
    }

    public void notifyRestoredStateEngine(HollowReadStateEngine restoredState, HollowReadStateEngine restoredNostreamsState) {
        this.hollowReadStateEngine = restoredState;
        this.nostreamsStateEngine = restoredNostreamsState;
    }

    public HollowReadStateEngine getNostreamsStateEngine() {
        return nostreamsStateEngine;
    }

    public synchronized void revertToPriorVersion() {
        Collection<LogTag> blobStateTags = Arrays.asList(RollbackStateEngine, BlobState);
        if (revertableStateEngine != null && revertableNostreamsStateEngine != null) {
            Map<String, String> curHeaders = BlobMetaDataUtil.fetchCoreHeaders(hollowReadStateEngine);
            Map<String, String> revHeaders = BlobMetaDataUtil.fetchCoreHeaders(revertableStateEngine);

            ctx.getLogger().error(blobStateTags, "Rolling back state engine in circuit breaker data provider from:{}, to:{}", curHeaders, revHeaders);
            hollowReadStateEngine = revertableStateEngine;
            nostreamsStateEngine = revertableNostreamsStateEngine;
        } else {
            boolean isMissing_revertableStateEngine = (null == revertableStateEngine);
            boolean isMissing_revertableNostreamsStateEngine = (null == revertableNostreamsStateEngine);
            ctx.getLogger().error(blobStateTags,
                    "Did NOT rollback state engine in circuit breaker data provider because revertableStateEngine( missing={} ) or revertableNostreamsStateEngine( missing={} )",
                    isMissing_revertableStateEngine,
                    isMissing_revertableNostreamsStateEngine);
        }

        // @TODO: WHY set to null??? - these should keep pointing to the prior state - memory optimization?
        if (ctx.getConfig().isHollowBlobDataProviderResetStateEnabled()) { // Condition to be backwards compatible
            revertableStateEngine = null;
            revertableNostreamsStateEngine = null;
        }
    }

    public HollowReadStateEngine getStateEngine() {
        return hollowReadStateEngine;
    }

    public int countItems(String typeName) {
        return hollowReadStateEngine.getTypeState(typeName).getPopulatedOrdinals().cardinality();
    }

    public void updateData(File snapshotFile, File deltaFile, File reverseDeltaFile, File nostreamsSnapshotFile, File nostreamsDeltaFile, File nostreamsReverseDeltaFile) throws IOException {
        if (deltaFile.exists() && snapshotFile.exists()) {
            validateChecksums(snapshotFile, deltaFile, reverseDeltaFile, nostreamsSnapshotFile, nostreamsDeltaFile, nostreamsReverseDeltaFile);
        } else if (snapshotFile.exists()) {
            hollowReadStateEngine = new HollowReadStateEngine();
            nostreamsStateEngine = new HollowReadStateEngine();
            readSnapshot(snapshotFile, hollowReadStateEngine);
            readSnapshot(nostreamsSnapshotFile, nostreamsStateEngine);
        } else {
            throw new RuntimeException("Neither snapshot, nor delta file found. Failing update.");
        }
    }



    private void validateChecksums(File snapshotFile, File deltaFile, File reverseDeltaFile, File nostreamsSnapshotFile, File nostreamsDeltaFile, File nostreamsReverseDeltaFile) throws IOException {
        FileStatLogger.logFileState(ctx.getLogger(), BlobChecksum, "validateChecksums", snapshotFile, deltaFile, reverseDeltaFile, nostreamsSnapshotFile, nostreamsDeltaFile, nostreamsReverseDeltaFile);

        Map<String, String> initRegularHeaders = BlobMetaDataUtil.fetchCoreHeaders(hollowReadStateEngine);
        Map<String, String> initNoStreamsHeaders = BlobMetaDataUtil.fetchCoreHeaders(nostreamsStateEngine);

        // -----------------------------------
        // Make sure reserve delta file exists
        if (deltaFile.exists() && !reverseDeltaFile.exists()) {
            throw new RuntimeException("Found deltaFile=" + deltaFile + " but missing reverseDeltaFile");
        }
        if (nostreamsDeltaFile.exists() && !nostreamsReverseDeltaFile.exists()) {
            throw new RuntimeException("Found nostreamsDeltaFile=" + nostreamsDeltaFile + " but missing nostreamsReverseDeltaFile");
        }

        // ----------------------
        // Handle new Snapshot State
        HollowReadStateEngine anotherStateEngine = new HollowReadStateEngine();
        HollowBlobReader anotherReader = new HollowBlobReader(anotherStateEngine);
        anotherReader.readSnapshot(ctx.files().newBlobInputStream(snapshotFile));

        HollowReadStateEngine anotherNostreamsStateEngine = new HollowReadStateEngine();
        HollowBlobReader anotherNostreamsReader = new HollowBlobReader(anotherNostreamsStateEngine);
        anotherNostreamsReader.readSnapshot(ctx.files().newBlobInputStream(nostreamsSnapshotFile));


        // ----------------------------------------------------------------------------------
        // Calculate Initial checksum for current state prior to applying Delta
        HollowChecksum initialChecksumBeforeDelta = null;
        HollowChecksum initialNostreamsChecksumBeforeDelta = null;
        if(reverseDeltaFile.exists()) {
            initialChecksumBeforeDelta = HollowChecksum.forStateEngineWithCommonSchemas(hollowReadStateEngine, anotherStateEngine);
            initialNostreamsChecksumBeforeDelta = HollowChecksum.forStateEngineWithCommonSchemas(nostreamsStateEngine, anotherNostreamsStateEngine);
        }

        // ------------------------------------------------------------------------------------------------
        // @TODO: WHY set to null??? - these should keep pointing to the prior state - memory optimization?
        if (ctx.getConfig().isHollowBlobDataProviderResetStateEnabled()) { // Condition to be backwards compatible
            revertableStateEngine = null;
            revertableNostreamsStateEngine = null;
        }

        // ---------------------------------------------------------------------------------------
        // Apply Delta to prior state and make sure its checksum is the same as new snapshot state
        {
            readDelta(deltaFile, hollowReadStateEngine);
            readDelta(nostreamsDeltaFile, nostreamsStateEngine);

            HollowChecksum deltaChecksum = HollowChecksum.forStateEngineWithCommonSchemas(hollowReadStateEngine, anotherStateEngine);
            HollowChecksum snapshotChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherStateEngine, hollowReadStateEngine);

            HollowChecksum nostreamsDeltaChecksum = HollowChecksum.forStateEngineWithCommonSchemas(nostreamsStateEngine, anotherNostreamsStateEngine);
            HollowChecksum nostreamsSnapshotChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherNostreamsStateEngine, nostreamsStateEngine);
            HollowChecksum clientFilteredSnapshotChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherStateEngine, nostreamsStateEngine);

            ctx.getLogger().info(BlobChecksum, "DELTA STATE CHECKSUM: {}", deltaChecksum);
            ctx.getLogger().info(BlobChecksum, "SNAPSHOT STATE CHECKSUM: {}", snapshotChecksum);
            ctx.getLogger().info(BlobChecksum, "NOSTREAMS DELTA STATE CHECKSUM: {}", nostreamsDeltaChecksum);
            ctx.getLogger().info(BlobChecksum, "NOSTREAMS SNAPSHOT STATE CHECKSUM: {}", nostreamsSnapshotChecksum);
            ctx.getLogger().info(BlobChecksum, "CLIENT FILTERED NOSTREAMS SNAPSHOT STATE CHECKSUM: {}", clientFilteredSnapshotChecksum);

            if (!deltaChecksum.equals(snapshotChecksum))
                throw new RuntimeException("DELTA CHECKSUM VALIDATION FAILURE!");
            if (!nostreamsDeltaChecksum.equals(nostreamsSnapshotChecksum))
                throw new RuntimeException("NOSTREAMS DELTA CHECKSUM VALIDATION FAILURE!");
            if (!clientFilteredSnapshotChecksum.equals(nostreamsSnapshotChecksum))
                throw new RuntimeException("NOSTREAMS/STREAMS CHECKSUM COMPARISON FAILURE!");
        }

        // ------------------------------------------------------------------------------
        // Apply Reserve Delta to modified state to make sure it gets back to prior state
        Collection<LogTag> blobStateTags = Arrays.asList(BlobChecksum, BlobState);
        {
            boolean isRevertableStateEngineCreated = false;
            if (reverseDeltaFile.exists()) {
                revertableStateEngine = processReverseDeltaFile(ctx, "", hollowReadStateEngine, reverseDeltaFile, anotherStateEngine, anotherReader, initialChecksumBeforeDelta);

                isRevertableStateEngineCreated = true;
                ctx.getLogger().info(blobStateTags, "revertableStateEngine({}) created from {}", BlobMetaDataUtil.fetchCoreHeaders(revertableStateEngine), reverseDeltaFile);
            } else {
                ctx.getLogger().warn(blobStateTags, "Reserve Delta File does not exists: {}", reverseDeltaFile);
            }

            boolean isNostreamsReverseDeltaFileCreated = false;
            if (nostreamsReverseDeltaFile.exists()) {
                revertableNostreamsStateEngine = processReverseDeltaFile(ctx, "NOSTREAMS", nostreamsStateEngine, nostreamsReverseDeltaFile, anotherNostreamsStateEngine, anotherNostreamsReader, initialNostreamsChecksumBeforeDelta);

                isNostreamsReverseDeltaFileCreated = true;
                ctx.getLogger().info(blobStateTags, "revertableNostreamsStateEngine({}) created from {}", BlobMetaDataUtil.fetchCoreHeaders(revertableNostreamsStateEngine), nostreamsReverseDeltaFile);
            } else {
                ctx.getLogger().warn(blobStateTags, "NoStreams Reserve Delta File does not exists: {}", nostreamsReverseDeltaFile);
            }

            // Make sure Revertable State Engine(s) were created
            if (deltaFile.exists() && !isRevertableStateEngineCreated) {
                ctx.getLogger().error(blobStateTags, "revertableStateEngine was not created");
                throw new RuntimeException("revertableStateEngine was not created");
            }

            if (nostreamsDeltaFile.exists() && !isNostreamsReverseDeltaFileCreated) {
                ctx.getLogger().error(blobStateTags, "revertableNostreamsStateEngine was not created");
                throw new RuntimeException("revertableNostreamsStateEngine was not created");
            }
        }

        ctx.getLogger().info(blobStateTags, "ReadState validate Completed - regular  : before({}), after({}), revertable({})", initRegularHeaders, BlobMetaDataUtil.fetchCoreHeaders(hollowReadStateEngine), BlobMetaDataUtil.fetchCoreHeaders(revertableStateEngine));
        ctx.getLogger().info(blobStateTags, "ReadState validate Completed - nostreams: before({}), after({}), revertable({}) ", initNoStreamsHeaders, BlobMetaDataUtil.fetchCoreHeaders(nostreamsStateEngine), BlobMetaDataUtil.fetchCoreHeaders(revertableNostreamsStateEngine));
    }

    private static HollowReadStateEngine processReverseDeltaFile(TransformerContext ctx, String prefix, HollowReadStateEngine hollowReadStateEngine, File reverseDeltaFile, HollowReadStateEngine anotherStateEngine, HollowBlobReader anotherReader, HollowChecksum initialChecksumBeforeDelta) throws IOException {
        Map<String, String> coreHeaders = BlobMetaDataUtil.fetchCoreHeaders(anotherStateEngine);
        try {
            anotherReader.applyDelta(ctx.files().newBlobInputStream(reverseDeltaFile));
        } catch (Exception ex) {
            ctx.getLogger().error(BlobState, "Failed apply ReverseDelta( {} ) to stateEngine( {} )", reverseDeltaFile, coreHeaders, ex);
            throw new IOException("Failed to apply ReverseDelta=" + reverseDeltaFile);
        }

        HollowChecksum reverseDeltaChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherStateEngine, hollowReadStateEngine);

        String context = prefix == null || prefix.trim().isEmpty() ? "" : prefix + " ";
        ctx.getLogger().info(BlobChecksum, "{}INITIAL STATE CHECKSUM: {}", context, initialChecksumBeforeDelta);
        ctx.getLogger().info(BlobChecksum, "{}REVERSE DELTA STATE CHECKSUM: {}", context, reverseDeltaChecksum);

        if (!initialChecksumBeforeDelta.equals(reverseDeltaChecksum))
            throw new RuntimeException(context + "REVERSE DELTA CHECKSUM VALIDATION FAILURE!");

        return anotherStateEngine;
    }


    private void readSnapshot(File snapshotFile, HollowReadStateEngine hollowReadStateEngine) throws IOException {
        Map<String, String> coreHeaders = BlobMetaDataUtil.fetchCoreHeaders(hollowReadStateEngine);
        try {
            ctx.getLogger().info(CircuitBreaker, "Reading Snapshot blob {}", snapshotFile.getName());
            HollowBlobReader hollowBlobReader = new HollowBlobReader(hollowReadStateEngine);
            hollowBlobReader.readSnapshot(ctx.files().newBlobInputStream(snapshotFile));
        } catch (Exception ex) {
            ctx.getLogger().error(BlobState, "Failed read Snapshot( {} ) to stateEngine( {} )", snapshotFile, coreHeaders, ex);
            throw new IOException("Failed to reader Snapshot=" + snapshotFile);
        }

    }

    private void readDelta(File deltaFile, HollowReadStateEngine hollowReadStateEngine) throws IOException {
        Map<String, String> coreHeaders = BlobMetaDataUtil.fetchCoreHeaders(hollowReadStateEngine);
        try {
            ctx.getLogger().info(CircuitBreaker, "Reading Delta blob {}", deltaFile.getName());
            HollowBlobReader hollowBlobReader = new HollowBlobReader(hollowReadStateEngine);
            hollowBlobReader.applyDelta(ctx.files().newBlobInputStream(deltaFile));
        } catch (Exception ex) {
            ctx.getLogger().error(BlobState, "Failed apply Delta( {} ) to stateEngine( {} )", deltaFile, coreHeaders, ex);
            throw new IOException("Failed to apply Delta=" + deltaFile);
        }
    }

}
