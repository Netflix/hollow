package com.netflix.vms.transformer.data.gen.videoAward;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoAwardListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("VideoAwardList", "VideoAwardMapping");

    private final List<VideoAwardMappingTestData> elements = new ArrayList<>();

    public VideoAwardListTestData(VideoAwardMappingTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static VideoAwardListTestData VideoAwardList(VideoAwardMappingTestData... elements) {
        return new VideoAwardListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(VideoAwardMappingTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}