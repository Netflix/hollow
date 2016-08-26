package com.netflix.vms.transformer.modules.packages.contracts;

import static com.netflix.vms.transformer.modules.packages.contracts.DownloadableAssetTypeIndex.Viewing.DOWNLOAD;
import static com.netflix.vms.transformer.modules.packages.contracts.DownloadableAssetTypeIndex.Viewing.STREAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.netflix.vms.transformer.contract.ContractAsset;

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

    public void mark(ContractAsset assetType, Viewing viewing) {
        DownloadableIdList idList = downloadableIdsByContract.get(assetType);
        if(idList != null)
            idList.mark(viewing);
    }

    public void markAll(Viewing viewing) {
        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            entry.getValue().mark(viewing);
        }
    }

    public void resetMarks() {
        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            entry.getValue().resetMark();
        }
    }

    public Set<com.netflix.vms.transformer.hollowoutput.Long> getAllUnmarked(Viewing viewing) {
        switch (viewing) {
        case STREAM:
            return getAllUnmarked();
        case DOWNLOAD:
            return getAllUnmarkedForDownloadAndMarkedForStreaming();
        default:
            throw new IllegalArgumentException(String.format("viewing=%s", viewing));
        }
    }

    public Set<com.netflix.vms.transformer.hollowoutput.Long> getAllUnmarked() {
        Set<com.netflix.vms.transformer.hollowoutput.Long> set = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            if(!entry.getValue().isMarked(STREAM))
                set.addAll(entry.getValue().getList());
        }

        return set;
    }

    public Set<com.netflix.vms.transformer.hollowoutput.Long> getAllUnmarkedForDownloadAndMarkedForStreaming() {
        Set<com.netflix.vms.transformer.hollowoutput.Long> set = new HashSet<com.netflix.vms.transformer.hollowoutput.Long>();

        for(Map.Entry<ContractAsset, DownloadableIdList> entry : downloadableIdsByContract.entrySet()) {
            if(!entry.getValue().isMarked(DOWNLOAD) && entry.getValue().isMarked(STREAM))
                set.addAll(entry.getValue().getList());
        }

        return set;
    }
    
    enum Viewing {
        STREAM,
        DOWNLOAD
    }

    private static class DownloadableIdList {

        private final List<com.netflix.vms.transformer.hollowoutput.Long> list;
        private boolean marked;
        private boolean markedForDownload;

        public DownloadableIdList() {
            this.list = new ArrayList<>();
        }

        public void addDownloadableId(long downloadableId) {
            list.add(new com.netflix.vms.transformer.hollowoutput.Long(downloadableId));
        }

        public void mark(Viewing viewing) {
            switch (viewing) {
            case STREAM:
                this.marked = true;
                break;
            case DOWNLOAD:
                this.markedForDownload = true;
                break;
            default:
                throw new IllegalArgumentException(String.format("viewing=%s", viewing));
            }
        }

        public void resetMark() {
            this.marked = false;
            this.markedForDownload = false;
        }

        public boolean isMarked(Viewing viewing) {
            switch (viewing) {
            case STREAM:
                return marked;
            case DOWNLOAD:
                return markedForDownload;
            default:
                throw new IllegalArgumentException(String.format("viewing=%s", viewing));
            }
        }
        
        public List<com.netflix.vms.transformer.hollowoutput.Long> getList() {
            return list;
        }
    }
}
