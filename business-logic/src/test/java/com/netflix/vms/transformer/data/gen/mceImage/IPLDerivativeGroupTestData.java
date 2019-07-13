package com.netflix.vms.transformer.data.gen.mceImage;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.mceImage.StringTestData.StringField;

public class IPLDerivativeGroupTestData extends HollowTestObjectRecord {

    IPLDerivativeGroupTestData(IPLDerivativeGroupField... fields){
        super(fields);
    }

    public static IPLDerivativeGroupTestData IPLDerivativeGroup(IPLDerivativeGroupField... fields) {
        return new IPLDerivativeGroupTestData(fields);
    }

    public IPLDerivativeGroupTestData update(IPLDerivativeGroupField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData externalIdRef() {
        Field f = super.getField("externalId");
        return f == null ? null : (StringTestData)f.value;
    }

    public String externalId() {
        Field f = super.getField("externalId");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int submission() {
        Field f = super.getField("submission");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public StringTestData imageTypeRef() {
        Field f = super.getField("imageType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String imageType() {
        Field f = super.getField("imageType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public IPLDerivativeSetTestData derivatives() {
        Field f = super.getField("derivatives");
        return f == null ? null : (IPLDerivativeSetTestData)f.value;
    }

    public static class IPLDerivativeGroupField extends Field {

        private IPLDerivativeGroupField(String name, Object val) { super(name, val); }

        public static IPLDerivativeGroupField externalId(StringTestData val) {
            return new IPLDerivativeGroupField("externalId", val);
        }

        public static IPLDerivativeGroupField externalId(StringField... fields) {
            return externalId(new StringTestData(fields));
        }

        public static IPLDerivativeGroupField externalId(String val) {
            return externalId(StringField.value(val));
        }

        public static IPLDerivativeGroupField submission(int val) {
            return new IPLDerivativeGroupField("submission", val);
        }

        public static IPLDerivativeGroupField imageType(StringTestData val) {
            return new IPLDerivativeGroupField("imageType", val);
        }

        public static IPLDerivativeGroupField imageType(StringField... fields) {
            return imageType(new StringTestData(fields));
        }

        public static IPLDerivativeGroupField imageType(String val) {
            return imageType(StringField.value(val));
        }

        public static IPLDerivativeGroupField derivatives(IPLDerivativeSetTestData val) {
            return new IPLDerivativeGroupField("derivatives", val);
        }

        public static IPLDerivativeGroupField derivatives(IPLArtworkDerivativeTestData... elements) {
            return derivatives(new IPLDerivativeSetTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("IPLDerivativeGroup", 4, new PrimaryKey("IPLDerivativeGroup", "externalId", "imageType", "submission"));

    static {
        SCHEMA.addField("externalId", FieldType.REFERENCE, "String");
        SCHEMA.addField("submission", FieldType.INT);
        SCHEMA.addField("imageType", FieldType.REFERENCE, "String");
        SCHEMA.addField("derivatives", FieldType.REFERENCE, "IPLDerivativeSet");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}