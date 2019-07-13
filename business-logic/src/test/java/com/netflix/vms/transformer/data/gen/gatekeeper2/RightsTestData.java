package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RightsTestData extends HollowTestObjectRecord {

    RightsTestData(RightsField... fields){
        super(fields);
    }

    public static RightsTestData Rights(RightsField... fields) {
        return new RightsTestData(fields);
    }

    public RightsTestData update(RightsField... fields){
        super.addFields(fields);
        return this;
    }

    public ListOfRightsWindowTestData windows() {
        Field f = super.getField("windows");
        return f == null ? null : (ListOfRightsWindowTestData)f.value;
    }

    public static class RightsField extends Field {

        private RightsField(String name, Object val) { super(name, val); }

        public static RightsField windows(ListOfRightsWindowTestData val) {
            return new RightsField("windows", val);
        }

        public static RightsField windows(RightsWindowTestData... elements) {
            return windows(new ListOfRightsWindowTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Rights", 1);

    static {
        SCHEMA.addField("windows", FieldType.REFERENCE, "ListOfRightsWindow");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}