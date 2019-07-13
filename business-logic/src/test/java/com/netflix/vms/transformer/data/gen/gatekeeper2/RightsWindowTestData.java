package com.netflix.vms.transformer.data.gen.gatekeeper2;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RightsWindowTestData extends HollowTestObjectRecord {

    RightsWindowTestData(RightsWindowField... fields){
        super(fields);
    }

    public static RightsWindowTestData RightsWindow(RightsWindowField... fields) {
        return new RightsWindowTestData(fields);
    }

    public RightsWindowTestData update(RightsWindowField... fields){
        super.addFields(fields);
        return this;
    }

    public long startDate() {
        Field f = super.getField("startDate");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public long endDate() {
        Field f = super.getField("endDate");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public Boolean onHold() {
        Field f = super.getField("onHold");
        return f == null ? null : (Boolean)f.value;
    }

    public ListOfRightsWindowContractTestData contractIdsExt() {
        Field f = super.getField("contractIdsExt");
        return f == null ? null : (ListOfRightsWindowContractTestData)f.value;
    }

    public static class RightsWindowField extends Field {

        private RightsWindowField(String name, Object val) { super(name, val); }

        public static RightsWindowField startDate(long val) {
            return new RightsWindowField("startDate", val);
        }

        public static RightsWindowField endDate(long val) {
            return new RightsWindowField("endDate", val);
        }

        public static RightsWindowField onHold(boolean val) {
            return new RightsWindowField("onHold", val);
        }

        public static RightsWindowField contractIdsExt(ListOfRightsWindowContractTestData val) {
            return new RightsWindowField("contractIdsExt", val);
        }

        public static RightsWindowField contractIdsExt(RightsWindowContractTestData... elements) {
            return contractIdsExt(new ListOfRightsWindowContractTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RightsWindow", 4);

    static {
        SCHEMA.addField("startDate", FieldType.LONG);
        SCHEMA.addField("endDate", FieldType.LONG);
        SCHEMA.addField("onHold", FieldType.BOOLEAN);
        SCHEMA.addField("contractIdsExt", FieldType.REFERENCE, "ListOfRightsWindowContract");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}