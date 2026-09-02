package com.netflix.hollow.api.client;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowSchema;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analyzes consumer state integrity using snapshot as source of truth.
 * <p>
 * Compares checksums at type-level granularity to identify which types
 * have diverged from the snapshot state. Returns detailed metrics about
 * which types need repair.
 * <p>
 * The actual state repair must be performed by the caller by reinitializing
 * the consumer state from the snapshot blob.
 */
public class HollowRepairApplier {
    private static final Logger LOG = Logger.getLogger(HollowRepairApplier.class.getName());

    /**
     * Repairs consumer state using snapshot state as source of truth.
     * <p>
     * Compares checksums at type-level granularity to identify mismatched types.
     * Returns detailed information about which types need repair.
     * <p>
     * NOTE: This method performs analysis and validation. The actual state
     * replacement must be performed by the caller by reinitializing the
     * consumer state from the snapshot.
     *
     * @param consumerState current (potentially corrupted) consumer state
     * @param snapshotState correct state from snapshot blob
     * @return repair result with success status and type-level metrics
     */
    public RepairResult repair(HollowReadStateEngine consumerState,
                               HollowReadStateEngine snapshotState) {
        LOG.log(Level.INFO, "Starting repair operation - comparing type checksums");

        Map<String, Integer> typesNeedingRepair = new HashMap<>();
        int totalTypesNeedingRepair = 0;

        try {
            // Iterate through all types in snapshot to identify mismatches
            for (HollowSchema schema : snapshotState.getSchemas()) {
                String typeName = schema.getName();

                HollowTypeReadState snapshotTypeState = snapshotState.getTypeState(typeName);
                HollowTypeReadState consumerTypeState = consumerState.getTypeState(typeName);

                if (consumerTypeState == null) {
                    LOG.log(Level.WARNING, String.format(
                        "Type %s exists in snapshot but missing in consumer state (schema evolution case)",
                        typeName));
                    continue;
                }

                // Compare type-level checksums
                boolean needsRepair = typeNeedsRepair(consumerTypeState, snapshotTypeState, schema);
                if (needsRepair) {
                    int populatedOrdinals = snapshotTypeState.getPopulatedOrdinals().cardinality();
                    typesNeedingRepair.put(typeName, populatedOrdinals);
                    totalTypesNeedingRepair++;
                    LOG.log(Level.INFO, String.format(
                        "Type %s has checksum mismatch - %d ordinals need repair",
                        typeName, populatedOrdinals));
                }
            }

            // Check for types in consumer that are missing in snapshot
            for (HollowSchema schema : consumerState.getSchemas()) {
                String typeName = schema.getName();
                if (snapshotState.getTypeState(typeName) == null) {
                    LOG.log(Level.WARNING, String.format(
                        "Type %s exists in consumer but missing in snapshot (filtered schema case)",
                        typeName));
                }
            }

            if (totalTypesNeedingRepair == 0) {
                LOG.log(Level.INFO, "No types need repair - all checksums match");
                return new RepairResult(true, 0, typesNeedingRepair);
            }

            LOG.log(Level.INFO, String.format(
                "Repair analysis complete. %d types need repair", totalTypesNeedingRepair));

            return new RepairResult(true, totalTypesNeedingRepair, typesNeedingRepair);

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Repair operation failed", e);
            return new RepairResult(false, 0, typesNeedingRepair);
        }
    }

    /**
     * Determines if a type needs repair by comparing checksums.
     * <p>
     * Computes and compares the checksum of the type state in both
     * consumer and snapshot engines.
     *
     * @param consumerType type state from consumer (potentially corrupted)
     * @param snapshotType type state from snapshot (source of truth)
     * @param schema schema to use for checksum computation
     * @return true if checksums differ (type needs repair), false otherwise
     */
    private boolean typeNeedsRepair(HollowTypeReadState consumerType,
                                    HollowTypeReadState snapshotType,
                                    HollowSchema schema) {
        try {
            // Compute checksums for both type states using the schema
            com.netflix.hollow.tools.checksum.HollowChecksum consumerChecksum =
                consumerType.getChecksum(schema);
            com.netflix.hollow.tools.checksum.HollowChecksum snapshotChecksum =
                snapshotType.getChecksum(schema);

            // Compare checksum values
            boolean checksumsDiffer = consumerChecksum.intValue() != snapshotChecksum.intValue();

            if (checksumsDiffer) {
                LOG.log(Level.FINE, String.format(
                    "Type %s checksum mismatch: consumer=%s, snapshot=%s",
                    schema.getName(),
                    consumerChecksum.toString(),
                    snapshotChecksum.toString()));
            }

            return checksumsDiffer;

        } catch (Exception e) {
            LOG.log(Level.WARNING, String.format(
                "Failed to compute checksum for type %s: %s",
                schema.getName(), e.getMessage()), e);
            // If we can't compute checksums, assume repair is needed to be safe
            return true;
        }
    }

    /**
     * Result of repair analysis operation.
     * <p>
     * Contains information about which types were identified as needing repair
     * based on checksum comparison.
     */
    public static class RepairResult {
        private final boolean success;
        private final int typesNeedingRepair;
        private final Map<String, Integer> ordinalsPerType;

        /**
         * Creates a repair result.
         *
         * @param success true if analysis completed successfully, false if error occurred
         * @param typesNeedingRepair number of types with checksum mismatches
         * @param ordinalsPerType map of type name to number of populated ordinals in that type
         */
        public RepairResult(boolean success, int typesNeedingRepair,
                           Map<String, Integer> ordinalsPerType) {
            this.success = success;
            this.typesNeedingRepair = typesNeedingRepair;
            this.ordinalsPerType = ordinalsPerType;
        }

        /**
         * @return true if repair analysis completed successfully (no exceptions)
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * @return number of types identified as needing repair
         */
        public int getTypesNeedingRepair() {
            return typesNeedingRepair;
        }

        /**
         * @return map of type name to number of populated ordinals, for types needing repair
         */
        public Map<String, Integer> getOrdinalsPerType() {
            return ordinalsPerType;
        }

        /**
         * @deprecated use {@link #getTypesNeedingRepair()} instead
         */
        @Deprecated
        public int getOrdinalsRepaired() {
            return typesNeedingRepair;
        }

        /**
         * @deprecated use {@link #getOrdinalsPerType()} instead
         */
        @Deprecated
        public Map<String, Integer> getOrdinalsRepairedByType() {
            return ordinalsPerType;
        }
    }
}
