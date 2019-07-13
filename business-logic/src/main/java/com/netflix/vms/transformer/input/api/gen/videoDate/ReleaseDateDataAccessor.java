package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ReleaseDateDataAccessor extends AbstractHollowDataAccessor<ReleaseDate> {

    public static final String TYPE = "ReleaseDate";
    private VideoDateAPI api;

    public ReleaseDateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VideoDateAPI)consumer.getAPI();
    }

    public ReleaseDateDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ReleaseDateDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ReleaseDateDataAccessor(HollowReadStateEngine rStateEngine, VideoDateAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ReleaseDate getRecord(int ordinal){
        return api.getReleaseDate(ordinal);
    }

}