package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoTypeDescriptorDataAccessor extends AbstractHollowDataAccessor<VideoTypeDescriptor> {

    public static final String TYPE = "VideoTypeDescriptor";
    private VideoTypeAPI api;

    public VideoTypeDescriptorDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoTypeAPI)consumer.getAPI();
    }

    public VideoTypeDescriptorDataAccessor(HollowReadStateEngine rStateEngine, VideoTypeAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoTypeDescriptorDataAccessor(HollowReadStateEngine rStateEngine, VideoTypeAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoTypeDescriptorDataAccessor(HollowReadStateEngine rStateEngine, VideoTypeAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoTypeDescriptor getRecord(int ordinal){
        return api.getVideoTypeDescriptor(ordinal);
    }

}