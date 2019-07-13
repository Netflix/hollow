package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoDateDataAccessor extends AbstractHollowDataAccessor<VideoDate> {

    public static final String TYPE = "VideoDate";
    private VideoDateAPI api;

    public VideoDateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoDateAPI)consumer.getAPI();
    }

    public VideoDateDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoDateDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoDateDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoDate getRecord(int ordinal){
        return api.getVideoDate(ordinal);
    }

}