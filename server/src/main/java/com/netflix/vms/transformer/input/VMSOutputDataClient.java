package com.netflix.vms.transformer.input;

import com.netflix.aws.file.FileStore;
import com.netflix.hollow.api.client.HollowClient;
import com.netflix.vms.transformer.util.HollowBlobKeybaseBuilder;

@Deprecated
public class VMSOutputDataClient extends HollowClient {

    //
    // This client is required to write transformer output to Filestore keyspace until all VMS Clients stop using
    // Filestore download
    //
    public VMSOutputDataClient(FileStore fileStore, String transformerVip) {
        super(new VMSDataTransitionCreator(fileStore, new HollowBlobKeybaseBuilder(transformerVip)));
    }
}
