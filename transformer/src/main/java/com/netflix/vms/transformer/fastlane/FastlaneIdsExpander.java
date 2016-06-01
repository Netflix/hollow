package com.netflix.vms.transformer.fastlane;

import com.netflix.vms.transformer.hollowinput.IndividualSupplementalHollow;

import com.netflix.vms.transformer.hollowinput.SupplementalsHollow;
import com.netflix.vms.transformer.hollowinput.EpisodeHollow;
import com.netflix.vms.transformer.hollowinput.SeasonHollow;
import com.netflix.vms.transformer.hollowinput.ShowSeasonEpisodeHollow;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;
import java.util.HashSet;
import java.util.Set;

public class FastlaneIdsExpander {
	
	private final VMSHollowInputAPI api;
	private final TransformerContext ctx;
	
	public FastlaneIdsExpander(VMSHollowInputAPI api, TransformerContext ctx) {
		this.api = api;
		this.ctx = ctx;
	}
	
	/// we need to build the closure of connected video IDs to the set of specified video IDs.
	public void expand() {
		
		Set<Integer> ids = new HashSet<Integer>(ctx.getFastlaneIds());
		
		boolean fastlaneIdsWereUpdated = true;
		
		/// keep iterating over all ShowSeasonEpisodes and adding any connected
		while(fastlaneIdsWereUpdated) {
			fastlaneIdsWereUpdated = false;
			for(ShowSeasonEpisodeHollow hierarchy : api.getAllShowSeasonEpisodeHollow()) {
				if(ids.contains((int)hierarchy._getMovieId())) {
					fastlaneIdsWereUpdated |= addHierarchy(hierarchy, ids);
					continue;
				}
				
				for(SeasonHollow season : hierarchy._getSeasons()) {
					if(ids.contains((int)season._getMovieId())) {
						fastlaneIdsWereUpdated |= addHierarchy(hierarchy, ids);
						continue;
					}
					
					for(EpisodeHollow ep : season._getEpisodes()) {
						if(ids.contains((int)ep._getMovieId())) {
							fastlaneIdsWereUpdated |= addHierarchy(hierarchy, ids);
							continue;
						}
					}
				}
			}
			
			for(SupplementalsHollow supplementals : api.getAllSupplementalsHollow()) {
				
				if(ids.contains((int)supplementals._getMovieId())) {
					fastlaneIdsWereUpdated |= addSupplementals(supplementals, ids);
					continue;
				}
				
				for(IndividualSupplementalHollow supp : supplementals._getSupplementals()) {
					if(ids.contains((int)supp._getMovieId())) {
						fastlaneIdsWereUpdated |= addSupplementals(supplementals, ids);
						continue;
					}
				}
				
			}
		}
		
		ctx.setFastlaneIds(ids);
	}
	
	private boolean addHierarchy(ShowSeasonEpisodeHollow hierarchy, Set<Integer> ids) {
		boolean fastlaneIdsWereUpdated = false;
		
		if(ids.add((int)hierarchy._getMovieId()))
			fastlaneIdsWereUpdated = true;
		
		for(SeasonHollow season : hierarchy._getSeasons()) {
			if(ids.add((int)season._getMovieId()))
				fastlaneIdsWereUpdated = true;
			
			for(EpisodeHollow episode : season._getEpisodes()) {
				if(ids.add((int)episode._getMovieId()))
					fastlaneIdsWereUpdated = true;
			}
		}
		
		return fastlaneIdsWereUpdated;
	}
	
	private boolean addSupplementals(SupplementalsHollow supplementals, Set<Integer> ids) {
		boolean fastlaneIdsWereUpdated = false;
		
		if(ids.add((int)supplementals._getMovieId()))
			fastlaneIdsWereUpdated = true;
		
		for(IndividualSupplementalHollow supp : supplementals._getSupplementals()) {
			if(ids.add((int)supp._getMovieId()))
				fastlaneIdsWereUpdated = true;
		}
		
		return fastlaneIdsWereUpdated;
	}

}
