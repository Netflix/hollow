package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class MapKeyTestData extends HollowTestObjectRecord {

    MapKeyTestData(MapKeyField... fields){
        super(fields);
    }

    public static MapKeyTestData MapKey(MapKeyField... fields) {
        return new MapKeyTestData(fields);
    }

    public static MapKeyTestData MapKey(String val) {
        return MapKey(MapKeyField.value(val));
    }

    public MapKeyTestData update(MapKeyField... fields){
        super.addFields(fields);
        return this;
    }

    public String value() {
        Field f = super.getField("value");
        return f == null ? null : (String)f.value;
    }

    public static class MapKeyField extends HollowTestObjectRecord.Field {

        private MapKeyField(String name, Object val) { super(name, val); }

        public static MapKeyField value(String val) {
            return new MapKeyField("value", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("MapKey", 1);

    static {
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}