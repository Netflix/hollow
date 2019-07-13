package com.netflix.vms.transformer.data.gen.packageDealCountry;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.packageDealCountry.LongTestData.LongField;
import com.netflix.vms.transformer.data.gen.packageDealCountry.StringTestData.StringField;

public class PackageMovieDealCountryGroupTestData extends HollowTestObjectRecord {

    PackageMovieDealCountryGroupTestData(PackageMovieDealCountryGroupField... fields){
        super(fields);
    }

    public static PackageMovieDealCountryGroupTestData PackageMovieDealCountryGroup(PackageMovieDealCountryGroupField... fields) {
        return new PackageMovieDealCountryGroupTestData(fields);
    }

    public PackageMovieDealCountryGroupTestData update(PackageMovieDealCountryGroupField... fields){
        super.addFields(fields);
        return this;
    }

    public LongTestData packageIdRef() {
        Field f = super.getField("packageId");
        return f == null ? null : (LongTestData)f.value;
    }

    public long packageId() {
        Field f = super.getField("packageId");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public LongTestData movieIdRef() {
        Field f = super.getField("movieId");
        return f == null ? null : (LongTestData)f.value;
    }

    public long movieId() {
        Field f = super.getField("movieId");
        if(f == null) return Long.MIN_VALUE;
        LongTestData ref = (LongTestData)f.value;
        return ref.value();
    }

    public StringTestData packageTypeRef() {
        Field f = super.getField("packageType");
        return f == null ? null : (StringTestData)f.value;
    }

    public String packageType() {
        Field f = super.getField("packageType");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData packageStatusRef() {
        Field f = super.getField("packageStatus");
        return f == null ? null : (StringTestData)f.value;
    }

    public String packageStatus() {
        Field f = super.getField("packageStatus");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public ListOfDealCountryGroupTestData dealCountryGroups() {
        Field f = super.getField("dealCountryGroups");
        return f == null ? null : (ListOfDealCountryGroupTestData)f.value;
    }

    public ListOfPackageTagsTestData tags() {
        Field f = super.getField("tags");
        return f == null ? null : (ListOfPackageTagsTestData)f.value;
    }

    public Boolean defaultPackage() {
        Field f = super.getField("defaultPackage");
        return f == null ? null : (Boolean)f.value;
    }

    public static class PackageMovieDealCountryGroupField extends Field {

        private PackageMovieDealCountryGroupField(String name, Object val) { super(name, val); }

        public static PackageMovieDealCountryGroupField packageId(LongTestData val) {
            return new PackageMovieDealCountryGroupField("packageId", val);
        }

        public static PackageMovieDealCountryGroupField packageId(LongField... fields) {
            return packageId(new LongTestData(fields));
        }

        public static PackageMovieDealCountryGroupField packageId(long val) {
            return packageId(LongField.value(val));
        }

        public static PackageMovieDealCountryGroupField movieId(LongTestData val) {
            return new PackageMovieDealCountryGroupField("movieId", val);
        }

        public static PackageMovieDealCountryGroupField movieId(LongField... fields) {
            return movieId(new LongTestData(fields));
        }

        public static PackageMovieDealCountryGroupField movieId(long val) {
            return movieId(LongField.value(val));
        }

        public static PackageMovieDealCountryGroupField packageType(StringTestData val) {
            return new PackageMovieDealCountryGroupField("packageType", val);
        }

        public static PackageMovieDealCountryGroupField packageType(StringField... fields) {
            return packageType(new StringTestData(fields));
        }

        public static PackageMovieDealCountryGroupField packageType(String val) {
            return packageType(StringField.value(val));
        }

        public static PackageMovieDealCountryGroupField packageStatus(StringTestData val) {
            return new PackageMovieDealCountryGroupField("packageStatus", val);
        }

        public static PackageMovieDealCountryGroupField packageStatus(StringField... fields) {
            return packageStatus(new StringTestData(fields));
        }

        public static PackageMovieDealCountryGroupField packageStatus(String val) {
            return packageStatus(StringField.value(val));
        }

        public static PackageMovieDealCountryGroupField dealCountryGroups(ListOfDealCountryGroupTestData val) {
            return new PackageMovieDealCountryGroupField("dealCountryGroups", val);
        }

        public static PackageMovieDealCountryGroupField dealCountryGroups(DealCountryGroupTestData... elements) {
            return dealCountryGroups(new ListOfDealCountryGroupTestData(elements));
        }

        public static PackageMovieDealCountryGroupField tags(ListOfPackageTagsTestData val) {
            return new PackageMovieDealCountryGroupField("tags", val);
        }

        public static PackageMovieDealCountryGroupField tags(StringTestData... elements) {
            return tags(new ListOfPackageTagsTestData(elements));
        }

        public static PackageMovieDealCountryGroupField defaultPackage(boolean val) {
            return new PackageMovieDealCountryGroupField("defaultPackage", val);
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("PackageMovieDealCountryGroup", 7, new PrimaryKey("PackageMovieDealCountryGroup", "movieId", "packageId"));

    static {
        SCHEMA.addField("packageId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("movieId", FieldType.REFERENCE, "Long");
        SCHEMA.addField("packageType", FieldType.REFERENCE, "String");
        SCHEMA.addField("packageStatus", FieldType.REFERENCE, "String");
        SCHEMA.addField("dealCountryGroups", FieldType.REFERENCE, "ListOfDealCountryGroup");
        SCHEMA.addField("tags", FieldType.REFERENCE, "ListOfPackageTags");
        SCHEMA.addField("defaultPackage", FieldType.BOOLEAN);
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}