package com.netflix.vms.transformer.data.gen.videoDate;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.videoDate.StringTestData.StringField;

public class VideoDateWindowTestData extends HollowTestObjectRecord {

    VideoDateWindowTestData(VideoDateWindowField... fields){
        super(fields);
    }

    public static VideoDateWindowTestData VideoDateWindow(VideoDateWindowField... fields) {
        return new VideoDateWindowTestData(fields);
    }

    public VideoDateWindowTestData update(VideoDateWindowField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData countryCodeRef() {
        Field f = super.getField("countryCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String countryCode() {
        Field f = super.getField("countryCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public ListOfReleaseDatesTestData releaseDates() {
        Field f = super.getField("releaseDates");
        return f == null ? null : (ListOfReleaseDatesTestData)f.value;
    }

    public static class VideoDateWindowField extends Field {

        private VideoDateWindowField(String name, Object val) { super(name, val); }

        public static VideoDateWindowField countryCode(StringTestData val) {
            return new VideoDateWindowField("countryCode", val);
        }

        public static VideoDateWindowField countryCode(StringField... fields) {
            return countryCode(new StringTestData(fields));
        }

        public static VideoDateWindowField countryCode(String val) {
            return countryCode(StringField.value(val));
        }

        public static VideoDateWindowField releaseDates(ListOfReleaseDatesTestData val) {
            return new VideoDateWindowField("releaseDates", val);
        }

        public static VideoDateWindowField releaseDates(ReleaseDateTestData... elements) {
            return releaseDates(new ListOfReleaseDatesTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("VideoDateWindow", 2);

    static {
        SCHEMA.addField("countryCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("releaseDates", FieldType.REFERENCE, "ListOfReleaseDates");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}