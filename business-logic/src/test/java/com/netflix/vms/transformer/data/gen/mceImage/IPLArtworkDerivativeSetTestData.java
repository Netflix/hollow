package com.netflix.vms.transformer.data.gen.mceImage;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.mceImage.StringTestData.StringField;

public class IPLArtworkDerivativeSetTestData extends HollowTestObjectRecord {

    IPLArtworkDerivativeSetTestData(IPLArtworkDerivativeSetField... fields){
        super(fields);
    }

    public static IPLArtworkDerivativeSetTestData IPLArtworkDerivativeSet(IPLArtworkDerivativeSetField... fields) {
        return new IPLArtworkDerivativeSetTestData(fields);
    }

    public IPLArtworkDerivativeSetTestData update(IPLArtworkDerivativeSetField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData derivativeSetIdRef() {
        Field f = super.getField("derivativeSetId");
        return f == null ? null : (StringTestData)f.value;
    }

    public String derivativeSetId() {
        Field f = super.getField("derivativeSetId");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public IPLDerivativeGroupSetTestData derivativesGroupBySource() {
        Field f = super.getField("derivativesGroupBySource");
        return f == null ? null : (IPLDerivativeGroupSetTestData)f.value;
    }

    public static class IPLArtworkDerivativeSetField extends Field {

        private IPLArtworkDerivativeSetField(String name, Object val) { super(name, val); }

        public static IPLArtworkDerivativeSetField derivativeSetId(StringTestData val) {
            return new IPLArtworkDerivativeSetField("derivativeSetId", val);
        }

        public static IPLArtworkDerivativeSetField derivativeSetId(StringField... fields) {
            return derivativeSetId(new StringTestData(fields));
        }

        public static IPLArtworkDerivativeSetField derivativeSetId(String val) {
            return derivativeSetId(StringField.value(val));
        }

        public static IPLArtworkDerivativeSetField derivativesGroupBySource(IPLDerivativeGroupSetTestData val) {
            return new IPLArtworkDerivativeSetField("derivativesGroupBySource", val);
        }

        public static IPLArtworkDerivativeSetField derivativesGroupBySource(IPLDerivativeGroupTestData... elements) {
            return derivativesGroupBySource(new IPLDerivativeGroupSetTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("IPLArtworkDerivativeSet", 2, new PrimaryKey("IPLArtworkDerivativeSet", "derivativeSetId"));

    static {
        SCHEMA.addField("derivativeSetId", FieldType.REFERENCE, "String");
        SCHEMA.addField("derivativesGroupBySource", FieldType.REFERENCE, "IPLDerivativeGroupSet");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}