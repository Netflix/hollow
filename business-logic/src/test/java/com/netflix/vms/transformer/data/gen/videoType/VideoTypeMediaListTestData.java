package com.netflix.vms.transformer.data.gen.videoType;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoTypeMediaListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("VideoTypeMediaList", "VideoTypeMedia");

    private final List<VideoTypeMediaTestData> elements = new ArrayList<>();

    public VideoTypeMediaListTestData(VideoTypeMediaTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static VideoTypeMediaListTestData VideoTypeMediaList(VideoTypeMediaTestData... elements) {
        return new VideoTypeMediaListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(VideoTypeMediaTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}