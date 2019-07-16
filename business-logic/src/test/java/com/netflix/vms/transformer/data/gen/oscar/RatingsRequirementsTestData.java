package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RatingsRequirementsTestData extends HollowTestObjectRecord {

    RatingsRequirementsTestData(RatingsRequirementsField... fields){
        super(fields);
    }

    public static RatingsRequirementsTestData RatingsRequirements(RatingsRequirementsField... fields) {
        return new RatingsRequirementsTestData(fields);
    }

    public static RatingsRequirementsTestData RatingsRequirements(String val) {
        return RatingsRequirements(RatingsRequirementsField._name(val));
    }

    public RatingsRequirementsTestData update(RatingsRequirementsField... fields){
        super.addFields(fields);
        return this;
    }

    public String _name() {
        Field f = super.getField("_name");
        return f == null ? null : (String)f.value;
    }

    public static class RatingsRequirementsField extends HollowTestObjectRecord.Field {

        private RatingsRequirementsField(String name, Object val) { super(name, val); }

        public static RatingsRequirementsField _name(String val) {
            return new RatingsRequirementsField("_name", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RatingsRequirements", 1);

    static {
        SCHEMA.addField("_name", FieldType.STRING);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}