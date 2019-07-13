package com.netflix.vms.transformer.data.gen.supplemental;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class SupplementalsTestData extends HollowTestObjectRecord {

    SupplementalsTestData(SupplementalsField... fields){
        super(fields);
    }

    public static SupplementalsTestData Supplementals(SupplementalsField... fields) {
        return new SupplementalsTestData(fields);
    }

    public SupplementalsTestData update(SupplementalsField... fields){
        super.addFields(fields);
        return this;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public SupplementalsListTestData supplementals() {
        Field f = super.getField("supplementals");
        return f == null ? null : (SupplementalsListTestData)f.value;
    }

    public static class SupplementalsField extends Field {

        private SupplementalsField(String name, Object val) { super(name, val); }

        public static SupplementalsField movieId(long val) {
            return new SupplementalsField("movieId", val);
        }

        public static SupplementalsField supplementals(SupplementalsListTestData val) {
            return new SupplementalsField("supplementals", val);
        }

        public static SupplementalsField supplementals(IndividualSupplementalTestData... elements) {
            return supplementals(new SupplementalsListTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Supplementals", 2, new PrimaryKey("Supplementals", "movieId"));

    static {
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("supplementals", FieldType.REFERENCE, "SupplementalsList");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}