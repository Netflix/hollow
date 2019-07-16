package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class TitleSourceTypeTestData extends HollowTestObjectRecord {

    TitleSourceTypeTestData(TitleSourceTypeField... fields){
        super(fields);
    }

    public static TitleSourceTypeTestData TitleSourceType(TitleSourceTypeField... fields) {
        return new TitleSourceTypeTestData(fields);
    }

    public static TitleSourceTypeTestData TitleSourceType(String val) {
        return TitleSourceType(TitleSourceTypeField._name(val));
    }

    public TitleSourceTypeTestData update(TitleSourceTypeField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class TitleSourceTypeField extends HollowTestObjectRecord.Field {

        private TitleSourceTypeField(String name, Object val) { super(name, val); }

        public static TitleSourceTypeField _name(String val) {
            return new TitleSourceTypeField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("TitleSourceType", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}