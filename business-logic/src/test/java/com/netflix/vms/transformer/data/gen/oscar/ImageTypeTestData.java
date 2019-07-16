package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class ImageTypeTestData extends HollowTestObjectRecord {

    ImageTypeTestData(ImageTypeField... fields){
        super(fields);
    }

    public static ImageTypeTestData ImageType(ImageTypeField... fields) {
        return new ImageTypeTestData(fields);
    }

    public static ImageTypeTestData ImageType(String val) {
        return ImageType(ImageTypeField.value(val));
    }

    public ImageTypeTestData update(ImageTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class ImageTypeField extends HollowTestObjectRecord.Field {

        private ImageTypeField(String name, Object val) { super(name, val); }

        public static ImageTypeField value(String val) {
            return new ImageTypeField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("ImageType", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}