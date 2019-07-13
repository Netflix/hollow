package com.netflix.vms.transformer.data.gen.mceImage;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.mceImage.StringTestData.StringField;

public class IPLArtworkDerivativeTestData extends HollowTestObjectRecord {

    IPLArtworkDerivativeTestData(IPLArtworkDerivativeField... fields){
        super(fields);
    }

    public static IPLArtworkDerivativeTestData IPLArtworkDerivative(IPLArtworkDerivativeField... fields) {
        return new IPLArtworkDerivativeTestData(fields);
    }

    public IPLArtworkDerivativeTestData update(IPLArtworkDerivativeField... fields){
        super.addFields(fields);
        return this;
    }

    public StringTestData recipeNameRef() {
        Field f = super.getField("recipeName");
        return f == null ? null : (StringTestData)f.value;
    }

    public String recipeName() {
        Field f = super.getField("recipeName");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public int widthInPixels() {
        Field f = super.getField("widthInPixels");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int heightInPixels() {
        Field f = super.getField("heightInPixels");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int targetWidthInPixels() {
        Field f = super.getField("targetWidthInPixels");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public int targetHeightInPixels() {
        Field f = super.getField("targetHeightInPixels");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public StringTestData recipeDescriptorRef() {
        Field f = super.getField("recipeDescriptor");
        return f == null ? null : (StringTestData)f.value;
    }

    public String recipeDescriptor() {
        Field f = super.getField("recipeDescriptor");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData cdnIdRef() {
        Field f = super.getField("cdnId");
        return f == null ? null : (StringTestData)f.value;
    }

    public String cdnId() {
        Field f = super.getField("cdnId");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData languageCodeRef() {
        Field f = super.getField("languageCode");
        return f == null ? null : (StringTestData)f.value;
    }

    public String languageCode() {
        Field f = super.getField("languageCode");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public ListOfDerivativeTagTestData modifications() {
        Field f = super.getField("modifications");
        return f == null ? null : (ListOfDerivativeTagTestData)f.value;
    }

    public ListOfDerivativeTagTestData overlayTypes() {
        Field f = super.getField("overlayTypes");
        return f == null ? null : (ListOfDerivativeTagTestData)f.value;
    }

    public static class IPLArtworkDerivativeField extends Field {

        private IPLArtworkDerivativeField(String name, Object val) { super(name, val); }

        public static IPLArtworkDerivativeField recipeName(StringTestData val) {
            return new IPLArtworkDerivativeField("recipeName", val);
        }

        public static IPLArtworkDerivativeField recipeName(StringField... fields) {
            return recipeName(new StringTestData(fields));
        }

        public static IPLArtworkDerivativeField recipeName(String val) {
            return recipeName(StringField.value(val));
        }

        public static IPLArtworkDerivativeField widthInPixels(int val) {
            return new IPLArtworkDerivativeField("widthInPixels", val);
        }

        public static IPLArtworkDerivativeField heightInPixels(int val) {
            return new IPLArtworkDerivativeField("heightInPixels", val);
        }

        public static IPLArtworkDerivativeField targetWidthInPixels(int val) {
            return new IPLArtworkDerivativeField("targetWidthInPixels", val);
        }

        public static IPLArtworkDerivativeField targetHeightInPixels(int val) {
            return new IPLArtworkDerivativeField("targetHeightInPixels", val);
        }

        public static IPLArtworkDerivativeField recipeDescriptor(StringTestData val) {
            return new IPLArtworkDerivativeField("recipeDescriptor", val);
        }

        public static IPLArtworkDerivativeField recipeDescriptor(StringField... fields) {
            return recipeDescriptor(new StringTestData(fields));
        }

        public static IPLArtworkDerivativeField recipeDescriptor(String val) {
            return recipeDescriptor(StringField.value(val));
        }

        public static IPLArtworkDerivativeField cdnId(StringTestData val) {
            return new IPLArtworkDerivativeField("cdnId", val);
        }

        public static IPLArtworkDerivativeField cdnId(StringField... fields) {
            return cdnId(new StringTestData(fields));
        }

        public static IPLArtworkDerivativeField cdnId(String val) {
            return cdnId(StringField.value(val));
        }

        public static IPLArtworkDerivativeField languageCode(StringTestData val) {
            return new IPLArtworkDerivativeField("languageCode", val);
        }

        public static IPLArtworkDerivativeField languageCode(StringField... fields) {
            return languageCode(new StringTestData(fields));
        }

        public static IPLArtworkDerivativeField languageCode(String val) {
            return languageCode(StringField.value(val));
        }

        public static IPLArtworkDerivativeField modifications(ListOfDerivativeTagTestData val) {
            return new IPLArtworkDerivativeField("modifications", val);
        }

        public static IPLArtworkDerivativeField modifications(DerivativeTagTestData... elements) {
            return modifications(new ListOfDerivativeTagTestData(elements));
        }

        public static IPLArtworkDerivativeField overlayTypes(ListOfDerivativeTagTestData val) {
            return new IPLArtworkDerivativeField("overlayTypes", val);
        }

        public static IPLArtworkDerivativeField overlayTypes(DerivativeTagTestData... elements) {
            return overlayTypes(new ListOfDerivativeTagTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("IPLArtworkDerivative", 10);

    static {
        SCHEMA.addField("recipeName", FieldType.REFERENCE, "String");
        SCHEMA.addField("widthInPixels", FieldType.INT);
        SCHEMA.addField("heightInPixels", FieldType.INT);
        SCHEMA.addField("targetWidthInPixels", FieldType.INT);
        SCHEMA.addField("targetHeightInPixels", FieldType.INT);
        SCHEMA.addField("recipeDescriptor", FieldType.REFERENCE, "String");
        SCHEMA.addField("cdnId", FieldType.REFERENCE, "String");
        SCHEMA.addField("languageCode", FieldType.REFERENCE, "String");
        SCHEMA.addField("modifications", FieldType.REFERENCE, "ListOfDerivativeTag");
        SCHEMA.addField("overlayTypes", FieldType.REFERENCE, "ListOfDerivativeTag");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}