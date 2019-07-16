package com.netflix.vms.transformer.data.gen.flexds;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.flexds.AuditGroupTestData.AuditGroupField;

public class DisplaySetTestData extends HollowTestObjectRecord {

    DisplaySetTestData(DisplaySetField... fields){
        super(fields);
    }

    public static DisplaySetTestData DisplaySet(DisplaySetField... fields) {
        return new DisplaySetTestData(fields);
    }

    public DisplaySetTestData update(DisplaySetField... fields){
        super.addFields(fields);
        return this;
    }

    public long setId() {
        Field f = super.getField("setId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public ListOfStringTestData countryCodes() {
        Field f = super.getField("countryCodes");
        return f == null ? null : (ListOfStringTestData)f.value;
    }

    public Boolean isDefault() {
        Field f = super.getField("isDefault");
        return f == null ? null : (Boolean)f.value;
    }

    public SetOfStringTestData displaySetTypes() {
        Field f = super.getField("displaySetTypes");
        return f == null ? null : (SetOfStringTestData)f.value;
    }

    public SetOfContainerTestData containers() {
        Field f = super.getField("containers");
        return f == null ? null : (SetOfContainerTestData)f.value;
    }

    public AuditGroupTestData created() {
        Field f = super.getField("created");
        return f == null ? null : (AuditGroupTestData)f.value;
    }

    public AuditGroupTestData updated() {
        Field f = super.getField("updated");
        return f == null ? null : (AuditGroupTestData)f.value;
    }

    public static class DisplaySetField extends HollowTestObjectRecord.Field {

        private DisplaySetField(String name, Object val) { super(name, val); }

        public static DisplaySetField setId(long val) {
            return new DisplaySetField("setId", val);
        }

        public static DisplaySetField countryCodes(ListOfStringTestData val) {
            return new DisplaySetField("countryCodes", val);
        }

        public static DisplaySetField countryCodes(StringTestData... elements) {
            return countryCodes(new ListOfStringTestData(elements));
        }

        public static DisplaySetField isDefault(boolean val) {
            return new DisplaySetField("isDefault", val);
        }

        public static DisplaySetField displaySetTypes(SetOfStringTestData val) {
            return new DisplaySetField("displaySetTypes", val);
        }

        public static DisplaySetField displaySetTypes(StringTestData... elements) {
            return displaySetTypes(new SetOfStringTestData(elements));
        }

        public static DisplaySetField containers(SetOfContainerTestData val) {
            return new DisplaySetField("containers", val);
        }

        public static DisplaySetField containers(ContainerTestData... elements) {
            return containers(new SetOfContainerTestData(elements));
        }

        public static DisplaySetField created(AuditGroupTestData val) {
            return new DisplaySetField("created", val);
        }

        public static DisplaySetField created(AuditGroupField... fields) {
            return created(new AuditGroupTestData(fields));
        }

        public static DisplaySetField updated(AuditGroupTestData val) {
            return new DisplaySetField("updated", val);
        }

        public static DisplaySetField updated(AuditGroupField... fields) {
            return updated(new AuditGroupTestData(fields));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("DisplaySet", 7, new PrimaryKey("DisplaySet", "setId"));

    static {
        SCHEMA.addField("setId", FieldType.LONG);
        SCHEMA.addField("countryCodes", FieldType.REFERENCE, "ListOfString");
        SCHEMA.addField("isDefault", FieldType.BOOLEAN);
        SCHEMA.addField("displaySetTypes", FieldType.REFERENCE, "SetOfString");
        SCHEMA.addField("containers", FieldType.REFERENCE, "SetOfContainer");
        SCHEMA.addField("created", FieldType.REFERENCE, "AuditGroup");
        SCHEMA.addField("updated", FieldType.REFERENCE, "AuditGroup");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}