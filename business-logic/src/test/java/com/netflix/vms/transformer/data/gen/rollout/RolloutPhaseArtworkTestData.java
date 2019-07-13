package com.netflix.vms.transformer.data.gen.rollout;

import com.netflix.hollow.api.testdata.HollowTestObjectRecord;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;

public class RolloutPhaseArtworkTestData extends HollowTestObjectRecord {

    RolloutPhaseArtworkTestData(RolloutPhaseArtworkField... fields){
        super(fields);
    }

    public static RolloutPhaseArtworkTestData RolloutPhaseArtwork(RolloutPhaseArtworkField... fields) {
        return new RolloutPhaseArtworkTestData(fields);
    }

    public RolloutPhaseArtworkTestData update(RolloutPhaseArtworkField... fields){
        super.addFields(fields);
        return this;
    }

    public RolloutPhaseArtworkSourceFileIdListTestData sourceFileIds() {
        Field f = super.getField("sourceFileIds");
        return f == null ? null : (RolloutPhaseArtworkSourceFileIdListTestData)f.value;
    }

    public static class RolloutPhaseArtworkField extends Field {

        private RolloutPhaseArtworkField(String name, Object val) { super(name, val); }

        public static RolloutPhaseArtworkField sourceFileIds(RolloutPhaseArtworkSourceFileIdListTestData val) {
            return new RolloutPhaseArtworkField("sourceFileIds", val);
        }

        public static RolloutPhaseArtworkField sourceFileIds(RolloutPhaseArtworkSourceFileIdTestData... elements) {
            return sourceFileIds(new RolloutPhaseArtworkSourceFileIdListTestData(elements));
        }

    }

    public static final HollowObjectSchema SCHEMA = new HollowObjectSchema("RolloutPhaseArtwork", 1);

    static {
        SCHEMA.addField("sourceFileIds", FieldType.REFERENCE, "RolloutPhaseArtworkSourceFileIdList");
    }

    @Override public HollowObjectSchema getSchema() { return SCHEMA; }

}