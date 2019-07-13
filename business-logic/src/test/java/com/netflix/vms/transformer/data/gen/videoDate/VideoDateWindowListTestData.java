package com.netflix.vms.transformer.data.gen.videoDate;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoDateWindowListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("VideoDateWindowList", "VideoDateWindow");

    private final List<VideoDateWindowTestData> elements = new ArrayList<>();

    public VideoDateWindowListTestData(VideoDateWindowTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static VideoDateWindowListTestData VideoDateWindowList(VideoDateWindowTestData... elements) {
        return new VideoDateWindowListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(VideoDateWindowTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}