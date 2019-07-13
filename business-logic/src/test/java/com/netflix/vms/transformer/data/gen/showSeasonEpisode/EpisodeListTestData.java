package com.netflix.vms.transformer.data.gen.showSeasonEpisode;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpisodeListTestData extends HollowTestRecord {

    private static final HollowListSchema SCHEMA = new HollowListSchema("EpisodeList", "Episode");

    private final List<EpisodeTestData> elements = new ArrayList<>();

    public EpisodeListTestData(EpisodeTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static EpisodeListTestData EpisodeList(EpisodeTestData... elements) {
        return new EpisodeListTestData(elements);
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowListWriteRecord rec = new HollowListWriteRecord();
        for(EpisodeTestData e : elements) {
            rec.addElement(e.addTo(writeEngine));
        }
        return rec;
    }

}