package com.netflix.vms.transformer.modules.packages.contracts;

import com.netflix.vms.transformer.contract.ContractAsset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DownloadableAssetTypeIndex {
    private final Map<ContractAsset, DownloadableIdList> downloadableIdsByContract;

    public DownloadableAssetTypeIndex() {
        this.downloadableIdsByContract = new HashMap<ContractAsset, DownloadableIdList>();
    }

    public void addDownloadableId(ContractAsset assetType, long downloadableId) {
        DownloadableIdList idList = downloadableIdsByContract.get(assetType);
        if(idList == null) {
            idList = new DownloadableIdList();
            downloadableIdsByContract.put(assetType, idList);
        }

        idList.addDownloadableId(downloadableId);
    }

    public void mark(ContractAsset assetType) {
        DownloadableIdList idList = downloadableIdsByContract.get(assetType);
        if(idList != null)
            idList.mark();
    }
    
    public void markForDownload(ContractAsset assetType) {
        DownloadableIdList idList = downloadableIdsByContract.get(assetType);
        if(idList != null)
            idList.markForDownload();
    }
    
    public void markAll() {
        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            entry.getValue().mark();
        }
    }

    public void resetMarks() {
        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            entry.getValue().resetMark();
        }
    }

    public Set<com.netflix.vms.transformer.hollowoutput.Long> getAllUnmarked() {
        Set<com.netflix.vms.transformer.hollowoutput.Long> set = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            if(!entry.getValue().isMarked())
                set.addAll(entry.getValue().getList());
        }

        return set;
    }

    public Set<com.netflix.vms.transformer.hollowoutput.Long> getAllUnmarkedForDownload() {
        Set<com.netflix.vms.transformer.hollowoutput.Long> set = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            if(!entry.getValue().isMarkedForDownload())
                set.addAll(entry.getValue().getList());
        }

        return set;
    }
    
    private static class DownloadableIdList {

        private final List<com.netflix.vms.transformer.hollowoutput.Long> list;
        private boolean marked;
        private boolean markedForDownload;

        public DownloadableIdList() {
            this.list = new ArrayList<com.netflix.vms.transformer.hollowoutput.Long>();
        }

        public void addDownloadableId(long downloadableId) {
            list.add(new com.netflix.vms.transformer.hollowoutput.Long(downloadableId));
        }

        public void mark() {
            this.marked = true;
        }
        
        public void markForDownload() {
            this.markedForDownload = true;
        }

        public void resetMark() {
            this.marked = false;
            this.markedForDownload = false;
        }

        public boolean isMarked() {
            return marked;
        }

        public boolean isMarkedForDownload() {
            return markedForDownload;
        }
        
        public List<com.netflix.vms.transformer.hollowoutput.Long> getList() {
            return list;
        }
    }
}
