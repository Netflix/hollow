package com.netflix.vms.transformer.input;

import com.netflix.hollow.netflixspecific.blob.store.NetflixS3BlobRetriever;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;

public class VMSOutputDataClient extends HollowClient {
    
    public VMSOutputDataClient(NetflixS3BlobRetriever retriever) {
        super(retriever);
    }
    
    public VMSOutputDataClient(String baseProxyURL, String localDataDir, String transformerVip) {
        super(null);
        //super(new VMSOutputDataProxyTransitionCreator(baseProxyURL, localDataDir, new HollowBlobKeybaseBuilder(transformerVip), true));
    }
    
}
