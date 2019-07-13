package com.netflix.vms.transformer.data.gen.videoGeneral;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoGeneralTitleTypeListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("VideoGeneralTitleTypeList", "VideoGeneralTitleType");

    private final List<VideoGeneralTitleTypeTestData> elements = new ArrayList<>();

    public VideoGeneralTitleTypeListTestData(VideoGeneralTitleTypeTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static VideoGeneralTitleTypeListTestData VideoGeneralTitleTypeList(VideoGeneralTitleTypeTestData... elements) {
        return new VideoGeneralTitleTypeListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(VideoGeneralTitleTypeTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}