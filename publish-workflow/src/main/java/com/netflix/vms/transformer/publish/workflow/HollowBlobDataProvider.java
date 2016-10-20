package com.netflix.vms.transformer.publish.workflow;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.BlobChecksum;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CircuitBreaker;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.RollbackStateEngine;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.netflix.hollow.read.engine.HollowBlobReader;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.hollow.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.util.HashCodes;
import com.netflix.hollow.util.HollowChecksum;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.ISOCountryHollow;
import com.netflix.vms.generated.notemplate.PackageDataHollow;
import com.netflix.vms.generated.notemplate.TopNVideoDataHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.generated.notemplate.VideoHollow;
import com.netflix.vms.transformer.common.TransformerContext;

public class HollowBlobDataProvider {
    /* dependencies */
    private final TransformerContext ctx;

    /* fields */
    private HollowReadStateEngine hollowReadStateEngine;
    
    private HollowReadStateEngine revertableStateEngine;

    public HollowBlobDataProvider(TransformerContext ctx) {
        this.ctx = ctx;
        this.hollowReadStateEngine = new HollowReadStateEngine(true);
    }
    
    public void notifyRestoredStateEngine(HollowReadStateEngine restoredState) {
        this.hollowReadStateEngine = restoredState;
    }

    public void readSnapshot(File snapshotFile) throws IOException {
        ctx.getLogger().info(CircuitBreaker, "Reading Snapshot blob {}", snapshotFile.getName());
        hollowReadStateEngine = new HollowReadStateEngine(true);
        HollowBlobReader hollowBlobReader = new HollowBlobReader(hollowReadStateEngine);
        hollowBlobReader.readSnapshot(ctx.files().newBlobInputStream(snapshotFile));
    }

    public void readDelta(File deltaFile) throws IOException {
        ctx.getLogger().info(CircuitBreaker, "Reading Delta blob {}", deltaFile.getName());
        HollowBlobReader hollowBlobReader = new HollowBlobReader(hollowReadStateEngine);
        hollowBlobReader.applyDelta(ctx.files().newBlobInputStream(deltaFile));
    }
    
    public synchronized void revertToPriorVersion() {
        if(revertableStateEngine != null) {
            ctx.getLogger().info(RollbackStateEngine, "Rolling back state engine in circuit breaker data provider");
            hollowReadStateEngine = revertableStateEngine;
        }
        revertableStateEngine = null;
    }

    public HollowReadStateEngine getStateEngine() {
        return hollowReadStateEngine;
    }

    public int countItems(String typeName) {
        return hollowReadStateEngine.getTypeState(typeName).getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals().cardinality();
    }

	public void updateData(File snapshotFile, File deltaFile, File reverseDeltaFile) throws IOException {
		if (deltaFile.exists() && snapshotFile.exists()) {
		    validateChecksums(snapshotFile, deltaFile, reverseDeltaFile);
        } else if (snapshotFile.exists()) {
            readSnapshot(snapshotFile);
        } else {
            throw new RuntimeException("Neither snapshot, nor delta file found. Failing update.");
        }
    }

    private void validateChecksums(File snapshotFile, File deltaFile, File reverseDeltaFile) throws IOException {
        HollowReadStateEngine anotherStateEngine = new HollowReadStateEngine();
        HollowBlobReader anotherReader = new HollowBlobReader(anotherStateEngine);
        anotherReader.readSnapshot(ctx.files().newBlobInputStream(snapshotFile));
        
        HollowChecksum initialChecksumBeforeDelta = null;
        if(reverseDeltaFile.exists())
            initialChecksumBeforeDelta = HollowChecksum.forStateEngineWithCommonSchemas(hollowReadStateEngine, anotherStateEngine);
        
        revertableStateEngine = null;
        
        readDelta(deltaFile);

        HollowChecksum deltaChecksum = HollowChecksum.forStateEngineWithCommonSchemas(hollowReadStateEngine, anotherStateEngine);
        HollowChecksum snapshotChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherStateEngine, hollowReadStateEngine);

        ctx.getLogger().info(BlobChecksum, "DELTA STATE CHECKSUM: {}", deltaChecksum);
        ctx.getLogger().info(BlobChecksum, "SNAPSHOT STATE CHECKSUM: {}", snapshotChecksum);

        if(!deltaChecksum.equals(snapshotChecksum))
            throw new RuntimeException("DELTA CHECKSUM VALIDATION FAILURE!");

        if(reverseDeltaFile.exists()) {
            anotherReader.applyDelta(ctx.files().newBlobInputStream(reverseDeltaFile));
            HollowChecksum reverseDeltaChecksum = HollowChecksum.forStateEngineWithCommonSchemas(anotherStateEngine, hollowReadStateEngine);

            ctx.getLogger().info(BlobChecksum, "INITIAL STATE CHECKSUM: {}", initialChecksumBeforeDelta);
            ctx.getLogger().info(BlobChecksum, "REVERSE DELTA STATE CHECKSUM: {}", reverseDeltaChecksum);

            if(!initialChecksumBeforeDelta.equals(reverseDeltaChecksum))
                throw new RuntimeException("REVERSE DELTA CHECKSUM VALIDATION FAILURE!");
            
            revertableStateEngine = anotherStateEngine;
        }
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
			PackageDataHollow packageData = (PackageDataHollow) api.getPackageDataHollow(ordinal);
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
