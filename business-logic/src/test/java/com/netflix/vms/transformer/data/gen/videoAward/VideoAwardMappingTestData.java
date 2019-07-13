package com.netflix.vms.transformer.data.gen.videoAward;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class VideoAwardMappingTestData extends HollowTestObjectRecord {

    VideoAwardMappingTestData(VideoAwardMappingField... fields){
        super(fields);
    }

    public static VideoAwardMappingTestData VideoAwardMapping(VideoAwardMappingField... fields) {
        return new VideoAwardMappingTestData(fields);
    }

    public VideoAwardMappingTestData update(VideoAwardMappingField... fields){
        super.addFields(fields);
        return this;
    }

    public long awardId() {
        Field f = super.getField("awardId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long personId() {
        Field f = super.getField("personId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public Boolean winner() {
        Field f = super.getField("winner");
        return f == null ? null : (Boolean)f.value;
    }

    public long year() {
        Field f = super.getField("year");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public static class VideoAwardMappingField extends Field {

        private VideoAwardMappingField(String name, Object val) { super(name, val); }

        public static VideoAwardMappingField awardId(long val) {
            return new VideoAwardMappingField("awardId", val);
        }

        public static VideoAwardMappingField personId(long val) {
            return new VideoAwardMappingField("personId", val);
        }

        public static VideoAwardMappingField sequenceNumber(long val) {
            return new VideoAwardMappingField("sequenceNumber", val);
        }

        public static VideoAwardMappingField winner(boolean val) {
            return new VideoAwardMappingField("winner", val);
        }

        public static VideoAwardMappingField year(long val) {
            return new VideoAwardMappingField("year", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoAwardMapping", 5);

    static {
        SCHEMA.addField("awardId", FieldType.LONG);
        SCHEMA.addField("personId", FieldType.LONG);
        SCHEMA.addField("sequenceNumber", FieldType.LONG);
        SCHEMA.addField("winner", FieldType.BOOLEAN);
        SCHEMA.addField("year", FieldType.LONG);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}