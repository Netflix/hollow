package com.netflix.vms.transformer.data.gen.supplemental;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.supplemental.StringTestData.StringField;

public class IndividualSupplementalTestData extends HollowTestObjectRecord {

    IndividualSupplementalTestData(IndividualSupplementalField... fields){
        super(fields);
    }

    public static IndividualSupplementalTestData IndividualSupplemental(IndividualSupplementalField... fields) {
        return new IndividualSupplementalTestData(fields);
    }

    public IndividualSupplementalTestData update(IndividualSupplementalField... fields){
        super.addFields(fields);
        return this;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public int sequenceNumber() {
        Field f = super.getField("sequenceNumber");
        return f == null ? Integer.MIN_VALUE : (Integer)f.value;
    }

    public StringTestData subTypeRef() {
        Field f = super.getField("subType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String subType() {
        Field f = super.getField("subType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public IndividualSupplementalThemeSetTestData themes() {
        Field f = super.getField("themes");
        return f == null ? null : (IndividualSupplementalThemeSetTestData)f.value;
    }

    public IndividualSupplementalIdentifierSetTestData identifiers() {
        Field f = super.getField("identifiers");
        return f == null ? null : (IndividualSupplementalIdentifierSetTestData)f.value;
    }

    public IndividualSupplementalUsageSetTestData usages() {
        Field f = super.getField("usages");
        return f == null ? null : (IndividualSupplementalUsageSetTestData)f.value;
    }

    public Boolean postplay() {
        Field f = super.getField("postplay");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean general() {
        Field f = super.getField("general");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean thematic() {
        Field f = super.getField("thematic");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean approvedForExploit() {
        Field f = super.getField("approvedForExploit");
        return f == null ? null : (Boolean)f.value;
    }

    public static class IndividualSupplementalField extends Field {

        private IndividualSupplementalField(String name, Object val) { super(name, val); }

        public static IndividualSupplementalField movieId(long val) {
            return new IndividualSupplementalField("movieId", val);
        }

        public static IndividualSupplementalField sequenceNumber(int val) {
            return new IndividualSupplementalField("sequenceNumber", val);
        }

        public static IndividualSupplementalField subType(StringTestData val) {
            return new IndividualSupplementalField("subType", val);
        }

        public static IndividualSupplementalField subType(StringField... fields) {
            return subType(new StringTestData(fields));
        }

        public static IndividualSupplementalField subType(String val) {
            return subType(StringField.value(val));
        }

        public static IndividualSupplementalField themes(IndividualSupplementalThemeSetTestData val) {
            return new IndividualSupplementalField("themes", val);
        }

        public static IndividualSupplementalField themes(StringTestData... elements) {
            return themes(new IndividualSupplementalThemeSetTestData(elements));
        }

        public static IndividualSupplementalField identifiers(IndividualSupplementalIdentifierSetTestData val) {
            return new IndividualSupplementalField("identifiers", val);
        }

        public static IndividualSupplementalField identifiers(StringTestData... elements) {
            return identifiers(new IndividualSupplementalIdentifierSetTestData(elements));
        }

        public static IndividualSupplementalField usages(IndividualSupplementalUsageSetTestData val) {
            return new IndividualSupplementalField("usages", val);
        }

        public static IndividualSupplementalField usages(StringTestData... elements) {
            return usages(new IndividualSupplementalUsageSetTestData(elements));
        }

        public static IndividualSupplementalField postplay(boolean val) {
            return new IndividualSupplementalField("postplay", val);
        }

        public static IndividualSupplementalField general(boolean val) {
            return new IndividualSupplementalField("general", val);
        }

        public static IndividualSupplementalField thematic(boolean val) {
            return new IndividualSupplementalField("thematic", val);
        }

        public static IndividualSupplementalField approvedForExploit(boolean val) {
            return new IndividualSupplementalField("approvedForExploit", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("IndividualSupplemental", 10);

    static {
        SCHEMA.addField("movieId", FieldType.LONG);
        SCHEMA.addField("sequenceNumber", FieldType.INT);
        SCHEMA.addField("subType", FieldType.REFERENCE, "String");
        SCHEMA.addField("themes", FieldType.REFERENCE, "IndividualSupplementalThemeSet");
        SCHEMA.addField("identifiers", FieldType.REFERENCE, "IndividualSupplementalIdentifierSet");
        SCHEMA.addField("usages", FieldType.REFERENCE, "IndividualSupplementalUsageSet");
        SCHEMA.addField("postplay", FieldType.BOOLEAN);
        SCHEMA.addField("general", FieldType.BOOLEAN);
        SCHEMA.addField("thematic", FieldType.BOOLEAN);
        SCHEMA.addField("approvedForExploit", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}