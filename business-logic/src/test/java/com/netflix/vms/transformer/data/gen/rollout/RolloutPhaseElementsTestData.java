package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.vms.transformer.data.gen.rollout.RolloutPhaseArtworkTestData.RolloutPhaseArtworkField;
import com.netflix.vms.transformer.data.gen.rollout.RolloutPhaseLocalizedMetadataTestData.RolloutPhaseLocalizedMetadataField;

public class RolloutPhaseElementsTestData extends HollowTestObjectRecord {

    RolloutPhaseElementsTestData(RolloutPhaseElementsField... fields){
        super(fields);
    }

    public static RolloutPhaseElementsTestData RolloutPhaseElements(RolloutPhaseElementsField... fields) {
        return new RolloutPhaseElementsTestData(fields);
    }

    public RolloutPhaseElementsTestData update(RolloutPhaseElementsField... fields){
        super.addFields(fields);
        return this;
    }

    public RolloutPhaseLocalizedMetadataTestData localized_metadata() {
        Field f = super.getField("localized_metadata");
        return f == null ? null : (RolloutPhaseLocalizedMetadataTestData)f.value;
    }

    public RolloutPhaseArtworkTestData artwork() {
        Field f = super.getField("artwork");
        return f == null ? null : (RolloutPhaseArtworkTestData)f.value;
    }

    public static class RolloutPhaseElementsField extends Field {

        private RolloutPhaseElementsField(String name, Object val) { super(name, val); }

        public static RolloutPhaseElementsField localized_metadata(RolloutPhaseLocalizedMetadataTestData val) {
            return new RolloutPhaseElementsField("localized_metadata", val);
        }

        public static RolloutPhaseElementsField localized_metadata(RolloutPhaseLocalizedMetadataField... fields) {
            return localized_metadata(new RolloutPhaseLocalizedMetadataTestData(fields));
        }

        public static RolloutPhaseElementsField artwork(RolloutPhaseArtworkTestData val) {
            return new RolloutPhaseElementsField("artwork", val);
        }

        public static RolloutPhaseElementsField artwork(RolloutPhaseArtworkField... fields) {
            return artwork(new RolloutPhaseArtworkTestData(fields));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhaseElements", 2);

    static {
        SCHEMA.addField("localized_metadata", FieldType.REFERENCE, "RolloutPhaseLocalizedMetadata");
        SCHEMA.addField("artwork", FieldType.REFERENCE, "RolloutPhaseArtwork");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}