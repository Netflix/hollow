package com.netflix.vms.transformer.data.gen.videoType;

import com.netflix.hollow.api.testdata.HollowTestRecord;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

public class VideoTypeDescriptorSetTestData extends HollowTestRecord {

    private static final HollowSetSchema SCHEMA = new HollowSetSchema("VideoTypeDescriptorSet", "VideoTypeDescriptor");

    private static ToIntFunction<VideoTypeDescriptorTestData> hashFunction = null;

    private final List<VideoTypeDescriptorTestData> elements = new ArrayList<>();

    public VideoTypeDescriptorSetTestData(VideoTypeDescriptorTestData... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    public static VideoTypeDescriptorSetTestData VideoTypeDescriptorSet(VideoTypeDescriptorTestData... elements) {
        return new VideoTypeDescriptorSetTestData(elements);
    }

    public static void setHashFunction(ToIntFunction<VideoTypeDescriptorTestData> f) {
        hashFunction = f;
    }

    @Override public HollowSchema getSchema() { return SCHEMA; }

    public HollowWriteRecord toWriteRecord(HollowWriteStateEngine writeEngine) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();
        for(VideoTypeDescriptorTestData e : elements) {
            if(hashFunction == null)
                rec.addElement(e.addTo(writeEngine));
            else
                rec.addElement(e.addTo(writeEngine), hashFunction.applyAsInt(e));
        }
        return rec;
    }

}