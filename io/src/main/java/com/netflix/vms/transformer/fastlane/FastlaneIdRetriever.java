package com.netflix.vms.transformer.fastlane;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.mutationstream.fastlane.FastlaneCassandraHelper;
import com.netflix.mutationstream.fastlane.FastlaneVideo;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class FastlaneIdRetriever {

	private final FastlaneCassandraHelper fastlaneCassandraHelper;
	
    @Inject
    public FastlaneIdRetriever(FastlaneCassandraHelper cassandraHelper) {
    	this.fastlaneCassandraHelper = cassandraHelper;
    }

	public Set<Integer> getFastlaneIds() {
		Set<Integer> ids = new HashSet<Integer>();
		
		try {
			for(FastlaneVideo vid : fastlaneCassandraHelper.getFastlaneVideos())
				ids.add(vid.getVideoId());
			
			return ids;
		} catch(ConnectionException ex) {
			throw new RuntimeException("Unable to retrieve FastLane IDs", ex);
		}
	}
    
}
