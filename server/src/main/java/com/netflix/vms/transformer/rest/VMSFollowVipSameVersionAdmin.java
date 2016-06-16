package com.netflix.vms.transformer.rest;

import java.util.Collections;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.aws.file.FileAccessItem;
import com.netflix.aws.file.FileStore;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.input.FileStoreUtil;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/vms/followvipsameversion")
public class VMSFollowVipSameVersionAdmin {
    
    
    private final FileStore fileStore;
    private final TransformerConfig config;
    
    @Inject
    public VMSFollowVipSameVersionAdmin(FileStore fileStore, TransformerConfig config) {
        this.fileStore = fileStore;
        this.config = config;
    }
    
    @GET
    @Produces({MediaType.TEXT_PLAIN})
    public Response findSameVersions(@QueryParam("fromVip") String fromVip, @QueryParam("toVip") String toVip) throws Exception {
        
        if(fromVip == null)
            fromVip = config.getTransformerVip();
        if(toVip == null)
            toVip = config.getFollowVip();
        
        
        Map<String, Long>fromVipVersions = getVersionsByInputParameters(fromVip);
        Map<String, Long>toVipVersions = getVersionsByInputParameters(toVip);
        
        List<VersionPair> versionPairs = new ArrayList<VersionPair>();
        
        for(Map.Entry<String, Long> entry : fromVipVersions.entrySet()) {
            Long fromVipVersion = entry.getValue();
            Long toVipVersion = toVipVersions.get(entry.getKey());
            
            if(toVipVersion != null)
                versionPairs.add(new VersionPair(fromVipVersion, toVipVersion));
        }
        
        Collections.sort(versionPairs);
        
        StringBuilder response = new StringBuilder();
        
        for(int i=0;i<versionPairs.size();i++) {
            response.append(versionPairs.get(i).getFromVersion()).append("=").append(versionPairs.get(i).getToVersion()).append("\n");
        }
        
        return Response.ok(response.toString(), MediaType.TEXT_PLAIN).build();
    }

    private final Map<String, Long> getVersionsByInputParameters(String vip) throws Exception {
        HollowBlobKeybaseBuilder keybaseBuilder = new HollowBlobKeybaseBuilder(vip);

        Map<String, Long> map = new HashMap<String, Long>();
        
        addVersionsByKeybase(map, keybaseBuilder.getSnapshotKeybase());
        addVersionsByKeybase(map, keybaseBuilder.getDeltaKeybase());
        
        return map;
    }

    private void addVersionsByKeybase(Map<String, Long> map, String snapshotKeybase) {
        List<FileAccessItem> allVersionItems = fileStore.getAllFileAccessItems(snapshotKeybase);
        
        for(FileAccessItem item : allVersionItems) {
            Long toVersion = FileStoreUtil.getToVersion(item);
            Long inputVersion = FileStoreUtil.getInputDataVersion(item);
            Long cycleDataTimestamp = FileStoreUtil.getPublishCycleDataTS(item);
            
            String key = inputVersion + "_" + cycleDataTimestamp;
            
            map.put(key, toVersion);
        }
    }
    
    
    private static class VersionPair implements Comparable<VersionPair> {
        private final long fromVersion;
        private final long toVersion;
        
        public VersionPair(long fromVersion, long toVersion) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
        }
        
        public long getFromVersion() {
            return fromVersion;
        }

        public long getToVersion() {
            return toVersion;
        }

        @Override
        public int compareTo(VersionPair other) {
            return Long.compare(other.fromVersion, fromVersion);
        }
    }
    
}
