package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VideoDateWindowDataAccessor extends AbstractHollowDataAccessor<VideoDateWindow> {

    public static final String TYPE = "VideoDateWindow";
    private VideoDateAPI api;

    public VideoDateWindowDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoDateAPI)consumer.getAPI();
    }

    public VideoDateWindowDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VideoDateWindowDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VideoDateWindowDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VideoDateWindow getRecord(int ordinal){
        return api.getVideoDateWindow(ordinal);
    }

}