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

public class VideoGeneralAliasListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("VideoGeneralAliasList", "VideoGeneralAlias");

    private final List<VideoGeneralAliasTestData> elements = new ArrayList<>();

    public VideoGeneralAliasListTestData(VideoGeneralAliasTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static VideoGeneralAliasListTestData VideoGeneralAliasList(VideoGeneralAliasTestData... elements) {
        return new VideoGeneralAliasListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(VideoGeneralAliasTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}