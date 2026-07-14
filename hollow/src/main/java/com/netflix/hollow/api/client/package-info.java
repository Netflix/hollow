/**
 * Hollow consumer client API for receiving and applying state transitions.
 *
 * <h2>Repair Transitions</h2>
 *
 * <p>Repair transitions enable automatic recovery from data integrity violations
 * detected via checksum validation. When a delta application results in state
 * that doesn't match the producer's checksum, a repair transition uses the
 * snapshot as source of truth to surgically fix corrupted ordinals.
 *
 * <h3>Enabling Repair</h3>
 *
 * <pre>{@code
 * HollowConsumer consumer = HollowConsumer
 *     .withBlobRetriever(retriever)
 *     .withChecksumValidation(true)  // Required
 *     .withRepairEnabled(true)        // Enable repair
 *     .build();
 * }</pre>
 *
 * <h3>Repair Flow</h3>
 *
 * <ol>
 *   <li>Delta applied from version N to N+1</li>
 *   <li>Consumer computes checksum and compares with producer's checksum</li>
 *   <li>On mismatch, repair transition triggered using snapshot at N+1</li>
 *   <li>{@link HollowRepairApplier} compares snapshot vs consumer state</li>
 *   <li>Only divergent ordinals are updated (surgical repair)</li>
 *   <li>Checksum re-validated after repair</li>
 * </ol>
 *
 * <h3>Key Classes</h3>
 *
 * <ul>
 *   <li>{@link ChecksumValidator} - Validates consumer state integrity</li>
 *   <li>{@link HollowRepairApplier} - Performs surgical state repair</li>
 *   <li>{@link HollowUpdatePlanner} - Plans update transitions including repair</li>
 *   <li>{@link HollowDataHolder} - Applies transitions to state engine</li>
 *   <li>{@link HollowClientUpdater} - Orchestrates update and repair operations</li>
 * </ul>
 *
 * <h3>Metrics and Observability</h3>
 *
 * <p>Custom metrics can be implemented by extending
 * {@link com.netflix.hollow.api.metrics.HollowConsumerMetrics} and overriding:
 * <ul>
 *   <li>{@code recordRepairTriggered(long version)} - Called when repair starts</li>
 *   <li>{@code recordRepairDuration(long version, long durationMs)} - Called after repair completes</li>
 *   <li>{@code recordRepairOrdinals(String typeName, int count)} - Called for each type repaired</li>
 *   <li>{@code recordChecksumMismatch(long version, long producer, long consumer)} - Called on checksum failure</li>
 * </ul>
 *
 * <h3>Listener Notifications</h3>
 *
 * <p>Implement {@link com.netflix.hollow.api.consumer.HollowConsumer.TransitionAwareRefreshListener}
 * to receive repair notifications via the {@code repairApplied()} callback.
 *
 * @see com.netflix.hollow.api.consumer.HollowConsumer
 * @see com.netflix.hollow.tools.checksum.HollowChecksum
 * @since 7.15.0
 */
package com.netflix.hollow.api.client;
