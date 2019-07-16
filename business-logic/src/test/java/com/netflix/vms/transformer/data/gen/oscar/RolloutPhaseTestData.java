package com.netflix.vms.transformer.data.gen.oscar;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

import com.netflix.vms.transformer.data.gen.oscar.DateTestData.DateField;
import com.netflix.vms.transformer.data.gen.oscar.MovieIdTestData.MovieIdField;
import com.netflix.vms.transformer.data.gen.oscar.PhaseNameTestData.PhaseNameField;
import com.netflix.vms.transformer.data.gen.oscar.PhaseTypeTestData.PhaseTypeField;
import com.netflix.vms.transformer.data.gen.oscar.StringTestData.StringField;
import com.netflix.vms.transformer.data.gen.oscar.WindowTypeTestData.WindowTypeField;

public class RolloutPhaseTestData extends HollowTestObjectRecord {

    RolloutPhaseTestData(RolloutPhaseField... fields){
        super(fields);
    }

    public static RolloutPhaseTestData RolloutPhase(RolloutPhaseField... fields) {
        return new RolloutPhaseTestData(fields);
    }

    public RolloutPhaseTestData update(RolloutPhaseField... fields){
        super.addFields(fields);
        return this;
    }

    public long rolloutPhaseId() {
        Field f = super.getField("rolloutPhaseId");
        return f == null ? Long.MIN_VALUE : (Long)f.value;
    }

    public PhaseNameTestData phaseNameRef() {
        Field f = super.getField("phaseName");
        return f == null ? null : (PhaseNameTestData)f.value;
    }

    public String phaseName() {
        Field f = super.getField("phaseName");
        if(f == null) return null;
        PhaseNameTestData ref = (PhaseNameTestData)f.value;
        return ref.value();
    }

    public DateTestData startDateRef() {
        Field f = super.getField("startDate");
        return f == null ? null : (DateTestData)f.value;
    }

    public long startDate() {
        Field f = super.getField("startDate");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public DateTestData endDateRef() {
        Field f = super.getField("endDate");
        return f == null ? null : (DateTestData)f.value;
    }

    public long endDate() {
        Field f = super.getField("endDate");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public WindowTypeTestData windowTypeRef() {
        Field f = super.getField("windowType");
        return f == null ? null : (WindowTypeTestData)f.value;
    }

    public String windowType() {
        Field f = super.getField("windowType");
        if(f == null) return null;
        WindowTypeTestData ref = (WindowTypeTestData)f.value;
        return ref._name();
    }

    public Boolean showCoreMetadata() {
        Field f = super.getField("showCoreMetadata");
        return f == null ? null : (Boolean)f.value;
    }

    public Boolean onHold() {
        Field f = super.getField("onHold");
        return f == null ? null : (Boolean)f.value;
    }

    public MovieIdTestData seasonMovieIdRef() {
        Field f = super.getField("seasonMovieId");
        return f == null ? null : (MovieIdTestData)f.value;
    }

    public long seasonMovieId() {
        Field f = super.getField("seasonMovieId");
        if(f == null) return Long.MIN_VALUE;
        MovieIdTestData ref = (MovieIdTestData)f.value;
        return ref.value();
    }

    public PhaseTypeTestData phaseTypeRef() {
        Field f = super.getField("phaseType");
        return f == null ? null : (PhaseTypeTestData)f.value;
    }

    public String phaseType() {
        Field f = super.getField("phaseType");
        if(f == null) return null;
        PhaseTypeTestData ref = (PhaseTypeTestData)f.value;
        return ref._name();
    }

    public SetOfPhaseTrailerTestData trailers() {
        Field f = super.getField("trailers");
        return f == null ? null : (SetOfPhaseTrailerTestData)f.value;
    }

    public SetOfPhaseCastMemberTestData castMembers() {
        Field f = super.getField("castMembers");
        return f == null ? null : (SetOfPhaseCastMemberTestData)f.value;
    }

    public SetOfPhaseMetadataElementTestData metadataElements() {
        Field f = super.getField("metadataElements");
        return f == null ? null : (SetOfPhaseMetadataElementTestData)f.value;
    }

    public SetOfPhaseArtworkTestData phaseArtworks() {
        Field f = super.getField("phaseArtworks");
        return f == null ? null : (SetOfPhaseArtworkTestData)f.value;
    }

    public SetOfPhaseRequiredImageTypeTestData requiredImageTypes() {
        Field f = super.getField("requiredImageTypes");
        return f == null ? null : (SetOfPhaseRequiredImageTypeTestData)f.value;
    }

    public DateTestData dateCreatedRef() {
        Field f = super.getField("dateCreated");
        return f == null ? null : (DateTestData)f.value;
    }

    public long dateCreated() {
        Field f = super.getField("dateCreated");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public DateTestData lastUpdatedRef() {
        Field f = super.getField("lastUpdated");
        return f == null ? null : (DateTestData)f.value;
    }

    public long lastUpdated() {
        Field f = super.getField("lastUpdated");
        if(f == null) return Long.MIN_VALUE;
        DateTestData ref = (DateTestData)f.value;
        return ref.value();
    }

    public StringTestData createdByRef() {
        Field f = super.getField("createdBy");
        return f == null ? null : (StringTestData)f.value;
    }

    public String createdBy() {
        Field f = super.getField("createdBy");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public StringTestData updatedByRef() {
        Field f = super.getField("updatedBy");
        return f == null ? null : (StringTestData)f.value;
    }

    public String updatedBy() {
        Field f = super.getField("updatedBy");
        if(f == null) return null;
        StringTestData ref = (StringTestData)f.value;
        return ref.value();
    }

    public static class RolloutPhaseField extends HollowTestObjectRecord.Field {

        private RolloutPhaseField(String name, Object val) { super(name, val); }

        public static RolloutPhaseField rolloutPhaseId(long val) {
            return new RolloutPhaseField("rolloutPhaseId", val);
        }

        public static RolloutPhaseField phaseName(PhaseNameTestData val) {
            return new RolloutPhaseField("phaseName", val);
        }

        public static RolloutPhaseField phaseName(PhaseNameField... fields) {
            return phaseName(new PhaseNameTestData(fields));
        }

        public static RolloutPhaseField phaseName(String val) {
            return phaseName(PhaseNameField.value(val));
        }

        public static RolloutPhaseField startDate(DateTestData val) {
            return new RolloutPhaseField("startDate", val);
        }

        public static RolloutPhaseField startDate(DateField... fields) {
            return startDate(new DateTestData(fields));
        }

        public static RolloutPhaseField startDate(long val) {
            return startDate(DateField.value(val));
        }

        public static RolloutPhaseField endDate(DateTestData val) {
            return new RolloutPhaseField("endDate", val);
        }

        public static RolloutPhaseField endDate(DateField... fields) {
            return endDate(new DateTestData(fields));
        }

        public static RolloutPhaseField endDate(long val) {
            return endDate(DateField.value(val));
        }

        public static RolloutPhaseField windowType(WindowTypeTestData val) {
            return new RolloutPhaseField("windowType", val);
        }

        public static RolloutPhaseField windowType(WindowTypeField... fields) {
            return windowType(new WindowTypeTestData(fields));
        }

        public static RolloutPhaseField windowType(String val) {
            return windowType(WindowTypeField._name(val));
        }

        public static RolloutPhaseField showCoreMetadata(boolean val) {
            return new RolloutPhaseField("showCoreMetadata", val);
        }

        public static RolloutPhaseField onHold(boolean val) {
            return new RolloutPhaseField("onHold", val);
        }

        public static RolloutPhaseField seasonMovieId(MovieIdTestData val) {
            return new RolloutPhaseField("seasonMovieId", val);
        }

        public static RolloutPhaseField seasonMovieId(MovieIdField... fields) {
            return seasonMovieId(new MovieIdTestData(fields));
        }

        public static RolloutPhaseField seasonMovieId(long val) {
            return seasonMovieId(MovieIdField.value(val));
        }

        public static RolloutPhaseField phaseType(PhaseTypeTestData val) {
            return new RolloutPhaseField("phaseType", val);
        }

        public static RolloutPhaseField phaseType(PhaseTypeField... fields) {
            return phaseType(new PhaseTypeTestData(fields));
        }

        public static RolloutPhaseField phaseType(String val) {
            return phaseType(PhaseTypeField._name(val));
        }

        public static RolloutPhaseField trailers(SetOfPhaseTrailerTestData val) {
            return new RolloutPhaseField("trailers", val);
        }

        public static RolloutPhaseField trailers(PhaseTrailerTestData... elements) {
            return trailers(new SetOfPhaseTrailerTestData(elements));
        }

        public static RolloutPhaseField castMembers(SetOfPhaseCastMemberTestData val) {
            return new RolloutPhaseField("castMembers", val);
        }

        public static RolloutPhaseField castMembers(PhaseCastMemberTestData... elements) {
            return castMembers(new SetOfPhaseCastMemberTestData(elements));
        }

        public static RolloutPhaseField metadataElements(SetOfPhaseMetadataElementTestData val) {
            return new RolloutPhaseField("metadataElements", val);
        }

        public static RolloutPhaseField metadataElements(PhaseMetadataElementTestData... elements) {
            return metadataElements(new SetOfPhaseMetadataElementTestData(elements));
        }

        public static RolloutPhaseField phaseArtworks(SetOfPhaseArtworkTestData val) {
            return new RolloutPhaseField("phaseArtworks", val);
        }

        public static RolloutPhaseField phaseArtworks(PhaseArtworkTestData... elements) {
            return phaseArtworks(new SetOfPhaseArtworkTestData(elements));
        }

        public static RolloutPhaseField requiredImageTypes(SetOfPhaseRequiredImageTypeTestData val) {
            return new RolloutPhaseField("requiredImageTypes", val);
        }

        public static RolloutPhaseField requiredImageTypes(PhaseRequiredImageTypeTestData... elements) {
            return requiredImageTypes(new SetOfPhaseRequiredImageTypeTestData(elements));
        }

        public static RolloutPhaseField dateCreated(DateTestData val) {
            return new RolloutPhaseField("dateCreated", val);
        }

        public static RolloutPhaseField dateCreated(DateField... fields) {
            return dateCreated(new DateTestData(fields));
        }

        public static RolloutPhaseField dateCreated(long val) {
            return dateCreated(DateField.value(val));
        }

        public static RolloutPhaseField lastUpdated(DateTestData val) {
            return new RolloutPhaseField("lastUpdated", val);
        }

        public static RolloutPhaseField lastUpdated(DateField... fields) {
            return lastUpdated(new DateTestData(fields));
        }

        public static RolloutPhaseField lastUpdated(long val) {
            return lastUpdated(DateField.value(val));
        }

        public static RolloutPhaseField createdBy(StringTestData val) {
            return new RolloutPhaseField("createdBy", val);
        }

        public static RolloutPhaseField createdBy(StringField... fields) {
            return createdBy(new StringTestData(fields));
        }

        public static RolloutPhaseField createdBy(String val) {
            return createdBy(StringField.value(val));
        }

        public static RolloutPhaseField updatedBy(StringTestData val) {
            return new RolloutPhaseField("updatedBy", val);
        }

        public static RolloutPhaseField updatedBy(StringField... fields) {
            return updatedBy(new StringTestData(fields));
        }

        public static RolloutPhaseField updatedBy(String val) {
            return updatedBy(StringField.value(val));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhase", 18);

    static {
        SCHEMA.addField("rolloutPhaseId", FieldType.LONG);
        SCHEMA.addField("phaseName", FieldType.REFERENCE, "PhaseName");
        SCHEMA.addField("startDate", FieldType.REFERENCE, "Date");
        SCHEMA.addField("endDate", FieldType.REFERENCE, "Date");
        SCHEMA.addField("windowType", FieldType.REFERENCE, "WindowType");
        SCHEMA.addField("showCoreMetadata", FieldType.BOOLEAN);
        SCHEMA.addField("onHold", FieldType.BOOLEAN);
        SCHEMA.addField("seasonMovieId", FieldType.REFERENCE, "MovieId");
        SCHEMA.addField("phaseType", FieldType.REFERENCE, "PhaseType");
        SCHEMA.addField("trailers", FieldType.REFERENCE, "SetOfPhaseTrailer");
        SCHEMA.addField("castMembers", FieldType.REFERENCE, "SetOfPhaseCastMember");
        SCHEMA.addField("metadataElements", FieldType.REFERENCE, "SetOfPhaseMetadataElement");
        SCHEMA.addField("phaseArtworks", FieldType.REFERENCE, "SetOfPhaseArtwork");
        SCHEMA.addField("requiredImageTypes", FieldType.REFERENCE, "SetOfPhaseRequiredImageType");
        SCHEMA.addField("dateCreated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("lastUpdated", FieldType.REFERENCE, "Date");
        SCHEMA.addField("createdBy", FieldType.REFERENCE, "String");
        SCHEMA.addField("updatedBy", FieldType.REFERENCE, "String");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}