package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.BlobChecksum;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CircuitBreaker;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.RollbackStateEngine;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.ISOCountryHollow;
import com.netflix.vms.generated.notemplate.PackageDataHollow;
import com.netflix.vms.generated.notemplate.TopNVideoDataHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.util.FileStatLogger;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    public synchronized void revertToPriorVersion() {
        if (revertableStateEngine != null && revertableNostreamsStateEngine != null) {
            ctx.getLogger().info(RollbackStateEngine, "Rolling back state engine in circuit breaker data provider");
            hollowReadStateEngine = revertableStateEngine;
            nostreamsStateEngine = revertableNostreamsStateEngine;
        } else {
            ctx.getLogger().info(RollbackStateEngine, "Did not rollback state engine in circuit breaker data provider because revertableStateEngine( missing={} ) or revertableNostreamsStateEngine( missing={} )",
                    null == revertableStateEngine,
                    null == revertableNostreamsStateEngine);
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
        {
            if (reverseDeltaFile.exists()) {
                revertableStateEngine = processReverseDeltaFile(ctx, "", hollowReadStateEngine, reverseDeltaFile, anotherStateEngine, anotherReader, initialChecksumBeforeDelta);
            } else {
                ctx.getLogger().warn(BlobChecksum, "Reserve Delta File does not exists: {}", reverseDeltaFile);
            }

            if (nostreamsReverseDeltaFile.exists()) {
                revertableNostreamsStateEngine = processReverseDeltaFile(ctx, "NOSTREAMS", nostreamsStateEngine, nostreamsReverseDeltaFile, anotherNostreamsStateEngine, anotherNostreamsReader, initialNostreamsChecksumBeforeDelta);
            } else {
                ctx.getLogger().warn(BlobChecksum, "NoStreams Reserve Delta File does not exists: {}", nostreamsReverseDeltaFile);
            }
        }
    }

    private static HollowReadStateEngine processReverseDeltaFile(TransformerContext ctx, String prefix, HollowReadStateEngine hollowReadStateEngine, File reverseDeltaFile, HollowReadStateEngine anotherStateEngine, HollowBlobReader anotherReader, HollowChecksum initialChecksumBeforeDelta) throws IOException {
        anotherReader.applyDelta(ctx.files().newBlobInputStream(reverseDeltaFile));
        HollowChecksum reverseDeltaChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherStateEngine, hollowReadStateEngine);

        String context = prefix == null || prefix.trim().isEmpty() ? "" : prefix + " ";
        ctx.getLogger().info(BlobChecksum, "{}INITIAL STATE CHECKSUM: {}", context, initialChecksumBeforeDelta);
        ctx.getLogger().info(BlobChecksum, "{}REVERSE DELTA STATE CHECKSUM: {}", context, reverseDeltaChecksum);

        if (!initialChecksumBeforeDelta.equals(reverseDeltaChecksum))
            throw new RuntimeException(context + "REVERSE DELTA CHECKSUM VALIDATION FAILURE!");

        return anotherStateEngine;
    }


    private void readSnapshot(File snapshotFile, HollowReadStateEngine hollowReadStateEngine) throws IOException {
        ctx.getLogger().info(CircuitBreaker, "Reading Snapshot blob {}", snapshotFile.getName());
        HollowBlobReader hollowBlobReader = new HollowBlobReader(hollowReadStateEngine);
        hollowBlobReader.readSnapshot(ctx.files().newBlobInputStream(snapshotFile));
    }

    private void readDelta(File deltaFile, HollowReadStateEngine hollowReadStateEngine) throws IOException {
        ctx.getLogger().info(CircuitBreaker, "Reading Delta blob {}", deltaFile.getName());
        HollowBlobReader hollowBlobReader = new HollowBlobReader(hollowReadStateEngine);
        hollowBlobReader.applyDelta(ctx.files().newBlobInputStream(deltaFile));
    }



    public Map<String, Set<Integer>> changedVideoCountryKeysBasedOnCompleteVideos() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) hollowReadStateEngine.getTypeState("CompleteVideo");
        PopulatedOrdinalListener completeVideoListener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet modifiedCompleteVideos = new BitSet(completeVideoListener.getPopulatedOrdinals().length());
        modifiedCompleteVideos.or(completeVideoListener.getPopulatedOrdinals());
        modifiedCompleteVideos.xor(completeVideoListener.getPreviousOrdinals());

        if(modifiedCompleteVideos.cardinality() == 0 || modifiedCompleteVideos.cardinality() == completeVideoListener.getPopulatedOrdinals().cardinality())
            return Collections.emptyMap();

        VMSRawHollowAPI api = new VMSRawHollowAPI(hollowReadStateEngine);
        Map<String, Set<Integer>> modifiedIds = new HashMap<>();

        int ordinal = modifiedCompleteVideos.nextSetBit(0);
        while(ordinal != -1) {
            CompleteVideoHollow cv = api.getCompleteVideoHollow(ordinal);

            int videoId = cv._getId()._getValue();
            String countryId = cv._getCountry()._getId();

            Set<Integer> videoIds = modifiedIds.get(countryId);
            if(videoIds == null) {
                videoIds = new HashSet<>();
                modifiedIds.put(countryId, videoIds);
            }
            videoIds.add(videoId);

            ordinal = modifiedCompleteVideos.nextSetBit(ordinal + 1);
        }

        return modifiedIds;
    }

    public Map<String, Set<Integer>> changedVideoCountryKeysBasedOnPackages() {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) hollowReadStateEngine.getTypeState("PackageData");
        PopulatedOrdinalListener packageListener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet modifiedPackages = new BitSet(packageListener.getPopulatedOrdinals().length());
        modifiedPackages.or(packageListener.getPopulatedOrdinals());
        modifiedPackages.xor(packageListener.getPreviousOrdinals());

        if (modifiedPackages.cardinality() == 0|| modifiedPackages.cardinality() == packageListener.getPopulatedOrdinals().cardinality())
            return Collections.emptyMap();

        Map<String, Set<Integer>> modifiedPackageVideoIds = new HashMap<>();
        VMSRawHollowAPI api = new VMSRawHollowAPI(hollowReadStateEngine);

        int ordinal = modifiedPackages.nextSetBit(0);
        while (ordinal != -1) {
            PackageDataHollow packageData = api.getPackageDataHollow(ordinal);
            Set<ISOCountryHollow> deployCountries = packageData._getAllDeployableCountries();
            VideoHollow video = packageData._getVideo();

            if (deployCountries == null || video == null)
                continue;

            Iterator<ISOCountryHollow> iterator = deployCountries.iterator();
            while (iterator.hasNext()) {
                String countryId = iterator.next()._getId();
                Set<Integer> modIdsForCountry = modifiedPackageVideoIds.get(countryId);
                if (modIdsForCountry == null) {
                    modIdsForCountry = new HashSet<Integer>();
                    modifiedPackageVideoIds.put(countryId, modIdsForCountry);
                }
                modIdsForCountry.add(video._getValue());
            }
            ordinal = modifiedPackages.nextSetBit(ordinal + 1);
        }
        return modifiedPackageVideoIds;
    }

    public static class VideoCountryKey {
        private final String country;
        private final int videoId;

        public VideoCountryKey(String country, int videoId) {
            this.country = country;
            this.videoId = videoId;
        }

        public String getCountry() {
            return country;
        }

        public int getVideoId() {
            return videoId;
        }

        @Override
        public int hashCode() {
            return HashCodes.hashInt(country.hashCode()) ^ HashCodes.hashInt(videoId);
        }

        @Override
        public boolean equals(Object other) {
            if(other instanceof VideoCountryKey) {
                return ((VideoCountryKey) other).getCountry().equals(country) && ((VideoCountryKey) other).getVideoId() == videoId;
            }
            return false;
        }
        @Override
        public String toString() {
            return "VideoCountryKey [country=" + country + ", videoId="
                    + videoId + "]";
        }

        public String toShortString() {
            return "["+country + ", "+ videoId + "]";
        }
    }

    public Map<String, TopNVideoDataHollow> getTopNData() {
        // Read from blob
        Map <String, TopNVideoDataHollow> result = new HashMap<>();

        VMSRawHollowAPI api = new VMSRawHollowAPI(hollowReadStateEngine);

        for(TopNVideoDataHollow topn: api.getAllTopNVideoDataHollow()){

            String countryId = topn._getCountryId();

            result.put(countryId, topn);

        }
        return result;
    }

}
