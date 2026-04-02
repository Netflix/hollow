# \[WIP\] Repair Transition Design

## Overview

A **repair transition** is a new transition type that surgically repairs in-memory consumer state using a snapshot as the source of truth, without requiring JVM restart. This enables automatic recovery from data integrity violations detected via checksums.

## Problem

Delta application can result in data integrity violations where consumer state diverges from producer state:

- Partial delta application failures
- Silent data corruption from library bugs
- Schema changes requiring additional fields from snapshots

Current recovery requires JVM restarts and takes potentially hours across a fleet of consumers. We desire sub-minute, zero-downtime repair.

## Solution Architecture

### 1\. Checksum-Based Detection

**Producer side:**

- Compute checksum for (1) the entire dataset and (2) each type after each publish cycle
- Store checksum in announcement metadata alongside version

**Consumer side:**

- Compute checksum after each delta application
- Compare against producer checksum from announcement
- Mismatch triggers repair transition

**Flow diagram:**

```
+-------------------------------------------------+
|                   PRODUCER                      |
+-------------------------------------------------+
|  1. Publish Cycle Completes                     |
|     \-> Compute Checksum: 0xABC123              |
|                                                 |
|  2. Store in Announcement                       |
|     \-> metadata["hollow.checksum"]="0xABC123"  |
|                                                 |
|  3. Publish Blobs                               |
|     |-> delta_v1_v2.blob                        |
|     |-> snapshot_v2.blob                        |
|     \-> announcement: {v2, checksum:0xABC123}   |
+-------------------------------------------------+
                      |
                      v
+-------------------------------------------------+
|                   CONSUMER                      |
+-------------------------------------------------+
|  1. Apply Delta (v1 -> v2)                      |
|     \-> State updated via delta                 |
|                                                 |
|  2. Compute Local Checksum                      |
|     \-> Consumer: 0xDEF456 [MISMATCH!]          |
|                                                 |
|  3. Compare with Producer Checksum              |
|     |-> Producer: 0xABC123                      |
|     |-> Consumer: 0xDEF456                      |
|     \-> MISMATCH -> Trigger Repair              |
|                                                 |
|  4. Execute Repair Transition                   |
|     |-> Load snapshot_v2.blob                   |
|     |-> Identify divergent types                |
|     |-> Reinitialize from snapshot              |
|     \-> Verify checksum: 0xABC123 [OK]          |
+-------------------------------------------------+
```

### 2\. Repair Transition Type

**New enum value:** `HollowConsumer.Blob.BlobType.REPAIR`

**Characteristics:**

- Has both `fromVersion` (corrupted) and `toVersion` (same as fromVersion)
- Uses snapshot blob at `toVersion` as source of truth
- Applied surgically, only updates types with checksum mismatches

**Transition comparison:**

```
+------------------------------------------+
|           TRANSITION TYPES               |
+------------------------------------------+

SNAPSHOT Transition:
  from: <none>, to: v2, blob: snapshot_v2.blob
  +-----+          +-----+
  | --- | -------> |  v2 |  Full state init
  +-----+          +-----+

DELTA Transition:
  from: v1, to: v2, blob: delta_v1_v2.blob
  +-----+          +-----+
  |  v1 | -------> |  v2 |  Incremental update
  +-----+          +-----+

REPAIR Transition:
  from: v2*, to: v2, blob: snapshot_v2.blob
  +-----+          +-----+
  | v2* | -------> |  v2 |  State correction
  +-----+          +-----+
  [BAD]            [OK]

  Process:
    1. Load snapshot -> temp state engine
    2. Compare per-type checksums
    3. Identify mismatches:
       |-> TypeA: 0x1234 vs 0x1234 [OK]
       |-> TypeB: 0x5678 vs 0xABCD [MISMATCH]
       \-> TypeC: 0x9999 vs 0x8888 [MISMATCH]
    4. Reinitialize from snapshot
    5. Verify overall checksum
```

### 2b\. Version Jumping with Repair Transitions

**New capability:** Repair transitions can now jump between versions (n → n±m) when delta chains are broken or unavailable.

**Use cases:**

1. **Forward jump with broken delta chain:**
   - Consumer at v100, wants v200
   - Delta v100→v101 is broken/deleted
   - REPAIR transition jumps v100 → v200 using snapshot_v200.blob

2. **Reverse jump without reverse deltas:**
   - Consumer at v200, wants to rollback to v100
   - Reverse delta unavailable
   - REPAIR transition jumps v200 → v100 using snapshot_v100.blob

**Comparison with Snapshot Fallback:**

```
SNAPSHOT Fallback (existing):
  Delta chain broken -> Fall back to snapshot transition
  from: <current>, to: <target>, blob: snapshot_<target>.blob
  Type: SNAPSHOT
  Use: Automatic fallback in regular update planning

REPAIR Version Jump (new):
  Delta chain broken -> Explicit repair transition
  from: <current>, to: <target>, blob: snapshot_<target>.blob
  Type: REPAIR
  Use: Explicit API call for version jumping with repair semantics
```

**API Usage:**

```java
// Explicit version jump using repair transition
HollowUpdatePlanner planner = new HollowUpdatePlanner(blobRetriever);

// Jump from v100 to v200 when delta chain broken
HollowUpdatePlan repairPlan = planner.planUpdateWithRepairJump(100L, 200L);

// Jump backward from v200 to v100
HollowUpdatePlan rollbackPlan = planner.planUpdateWithRepairJump(200L, 100L);
```

**Behavior:**

- If `currentVersion == desiredVersion`: Returns `HollowUpdatePlan.DO_NOTHING`
- If snapshot unavailable at target: Falls back to regular `planUpdate()` (may use delta path or snapshot fallback)
- If snapshot available: Creates single REPAIR transition with `fromVersion != toVersion`
- Logs: `"Created repair transition plan for version jump: <from> -> <to>"`

**Key differences from same-version repair:**

| Aspect | Same-Version Repair | Version Jump Repair |
|--------|---------------------|---------------------|
| Purpose | Fix corruption at same version | Jump versions when delta unavailable |
| Versions | fromVersion == toVersion | fromVersion != toVersion |
| Trigger | Checksum mismatch detection | Explicit API call or broken delta chain |
| Snapshot | Uses snapshot at current version | Uses snapshot at target version |
| API Method | `planUpdateWithRepair()` | `planUpdateWithRepairJump()` |

### 3\. Update Plan Integration

**HollowUpdatePlanner enhancement:**

- After delta to version V, if checksum mismatch detected, insert `REPAIR` transition
- Plan becomes: `[DELTA(v1->v2), REPAIR(v2->v2)]`
- Planner retrieves snapshot blob for v2 to use in repair

**Update plan verification:**

- Existing `UpdatePlanBlobVerifier` can verify repair blobs exist
- Fail fast if snapshot unavailable for repair

**Update plan flow:**

```
Normal Update (No Corruption):
+------------------------------------+
|  Plan: [DELTA(v1->v2)]             |
+------------------------------------+
              |
              v
        +----------+
        |  Apply   |  State: v1 -> v2
        |  Delta   |  Checksum: [OK]
        +----------+
              |
              v
          [Complete]


Update with Corruption:
+------------------------------------+
|  Plan: [DELTA(v1->v2)]             |
+------------------------------------+
              |
              v
        +----------+
        |  Apply   |  State: v1 -> v2*
        |  Delta   |  Checksum: [MISMATCH!]
        +----------+
              |
              v
+------------------------------------+
|  Planner inserts REPAIR transition |
|  Plan: [DELTA(v1->v2),REPAIR(v2->v2)]|
+------------------------------------+
              |
              v
        +----------+
        |  Apply   |  1. Load snapshot
        |  Repair  |  2. Analyze checksums
        |          |  3. Reinitialize
        +----------+  4. Verify: [OK]
              |
              v
          [Complete]


Blob Verification:
+------------------------------------+
|  UpdatePlanBlobVerifier            |
+------------------------------------+
|  Plan: [DELTA(v1->v2),REPAIR(v2->v2)]|
|                                    |
|  [OK] delta_v1_v2.blob exists      |
|  [OK] snapshot_v2.blob exists      |
|       ^ REQUIRED for REPAIR        |
|                                    |
|  If snapshot unavailable:          |
|    -> Fail fast with exception     |
+------------------------------------+
```

### 4\. Repair Application Logic

**New class:** `HollowRepairApplier` in `com.netflix.hollow.api.client`

**Process:**

1. Caller loads snapshot blob into temporary `HollowReadStateEngine`
2. Caller invokes `HollowRepairApplier.repair(consumerState, snapshotState)`
3. Repair applier computes per-type checksums for both consumer and snapshot states
4. For each type, compare checksums and identify mismatches
5. Return `RepairResult` with list of types needing repair
6. Caller reinitializes consumer state from snapshot blob
7. Caller verifies overall checksum now matches

**Detailed repair flow:**

```
+-------------------------------------+
|     REPAIR TRANSITION EXECUTION     |
+-------------------------------------+

Step 1: Load Snapshot
+------------------+
| snapshot_v2.blob |
+--------+---------+
         |
         v
   +----------+
   | HollowBlob| readSnapshot()
   |  Reader   | --------------+
   +-----------+               |
                               v
                   +--------------------+
                   |  Snapshot State    |
                   |  TypeA: [ordinals] |
                   |  TypeB: [ordinals] |
                   |  TypeC: [ordinals] |
                   +--------------------+

Step 2: Analyze Type Checksums
+-----------------+    +-----------------+
| Consumer State  | vs | Snapshot State  |
| TypeA: 0x1234   |    | TypeA: 0x1234   |
|        [OK]     |    |        [OK]     |
| TypeB: 0x5678   |    | TypeB: 0xABCD   |
|        [BAD]    |    |        [OK]     |
| TypeC: 0x9999   |    | TypeC: 0x8888   |
|        [BAD]    |    |        [OK]     |
+-----------------+    +-----------------+
         |                      |
         +----------+-----------+
                    v
          +------------------+
          | HollowRepair     |
          |  Applier.repair()|
          +---------+--------+
                    |
                    v
       +-----------------------+
       |  RepairResult         |
       |  success: true        |
       |  typesNeedingRepair:2 |
       |  TypeB: 1,523 ord     |
       |  TypeC: 842 ord       |
       +-----------------------+

Step 3: Reinitialize Consumer State
+-----------------+    +-----------------+
| Consumer State  |    | snapshot_v2.blob|
| (corrupted v2)  |    +--------+--------+
| TypeA: 0x1234   |             |
|        [OK]     |             | readSnapshot()
| TypeB: 0x5678   |             |
|        [BAD]    |             v
| TypeC: 0x9999   |    +-----------------+
|        [BAD]    |    | HollowBlobReader|
+--------+--------+    +--------+--------+
         | invalidate()         |
         v                      v
+-----------------+    +-----------------+
| Consumer State  |<---| Fresh State from|
| (reinitialized) |    |    Snapshot     |
| TypeA: 0x1234   |    | TypeA: 0x1234   |
|        [OK]     |    |        [OK]     |
| TypeB: 0xABCD   |    | TypeB: 0xABCD   |
|        [OK]     |    |        [OK]     |
| TypeC: 0x8888   |    | TypeC: 0x8888   |
|        [OK]     |    |        [OK]     |
+-----------------+    +-----------------+

Step 4: Verify Final Checksum
+-----------------+
| Consumer State  |
| Overall:        |
| 0xABC123 [OK]   |
| (matches prod)  |
+-----------------+
```

**Type-level analysis approach:**

- Compares checksums at type granularity, not ordinal-level
- Identifies exactly which types have diverged
- Provides detailed metrics for observability
- Simple implementation with clear success criteria

**Actual repair mechanism:**

- Caller reinitializes the entire consumer state from snapshot
- This is simpler and safer than in-place type state replacement
- Avoids complexity of managing cross-type references during partial updates
- Leverages existing snapshot loading infrastructure

**Alternative approach (ordinal-level):**

- For scenarios requiring minimal repair scope, ordinal-by-ordinal comparison is theoretically possible
- Would iterate through populated ordinals and copy only divergent data
- Significantly more complex implementation
- Would require careful handling of cross-ordinal references

**Error handling:**

1. **Snapshot unavailable**: Caller fails fast before invoking repair applier
2. **Type missing in snapshot**: Log warning and skip type (schema evolution case)
3. **Type missing in consumer**: Log warning (filtered schema case)
4. **Checksum computation failure**: Catch exception, log warning, assume repair needed for safety
5. **Checksum still mismatched after repair**: Caller throws exception after reinitializing from snapshot
6. **Analysis failure**: Return RepairResult with success=false

### 5\. Listener Invocation

**New method:** `TransitionAwareRefreshListener.repairApplied(fromVersion, toVersion, transitionBlob)`

**Invocation sequence:**

```
deltaApplied(v1, v2, deltaBlob)
[checksum mismatch detected]
repairApplied(v2, v2, repairBlob)
```

## API Changes

### Producer API

```java
// Existing HollowProducer automatically computes checksums
// Store in announcement metadata:
Map<String, String> metadata = new HashMap<>();
metadata.put("checksum", String.valueOf(checksum.getChecksum()));
announcer.announce(version, metadata);
```

### Consumer API

```java
// New listener method
interface TransitionAwareRefreshListener {
    void repairApplied(long fromVersion, long toVersion, Blob transitionBlob);
}

// New configuration
HollowConsumer.Builder builder = HollowConsumer
    .withBlobRetriever(retriever)
    .withRepairEnabled(true)  // Enable automatic repair
    .withChecksumValidation(true);  // Enable checksum validation
```

## Performance Optimizations (PoC Features)

> **Note:** These are proof-of-concept implementations. See "Production Readiness" section below for known limitations.

### Incremental Per-Type Checksum Validation

Instead of computing full state checksums after every delta, consumers can validate individual type checksums for faster mismatch detection.

**Producer side:**
```java
// Per-type checksums automatically published in announcement metadata
// Keys: hollow.checksum.TypeA, hollow.checksum.TypeB, etc.
```

**Consumer side:**
```java
ChecksumValidator validator = new ChecksumValidator();
ChecksumValidator.IncrementalResult result = validator.validateIncremental(
    stateEngine, announcementMetadata, computedChecksum);

if (!result.isValid()) {
    System.out.println("Mismatched types: " + result.getMismatchedTypes());
    // Trigger repair for specific types only
}
```

**Benefits:**
- Faster validation (checks only divergent types)
- Identifies exactly which types need repair
- Reduces checksum computation overhead

### Checksum-Based Transition Fallback

When delta chains are unavailable or broken, the planner automatically falls back to snapshot transitions. This feature adds observability to the existing fallback mechanism through enhanced logging.

**Implementation:** The existing `HollowUpdatePlanner.planUpdate()` method already contained fallback logic. The PoC enhancement adds INFO-level logging to make fallback behavior visible:

```java
// Existing fallback behavior with added visibility
HollowUpdatePlanner planner = new HollowUpdatePlanner(retriever, ...);
HollowUpdatePlan plan = planner.planUpdate(currentVersion, desiredVersion, true);

// Logs when delta path cannot reach desired version
// Logs when using snapshot transition fallback
// Validates checksum after transition (for delta plans)
```

**Use cases:**
- Delta blobs expired or deleted
- Reverse delta unavailable for rollback
- Faster recovery path when delta chain long
- Debugging transition path selection

### Type-by-Type Memory Profiling

Profile heap allocation during type loading to identify memory hotspots and optimize large datasets.

```java
TypeMemoryProfiler profiler = new TypeMemoryProfiler();
profiler.startProfiling(stateEngine);

// Load snapshot or apply delta
consumer.triggerRefreshTo(version);

TypeMemoryProfiler.ProfileResult result = profiler.endProfiling();
result.printSummary();

// Output:
// === Type Memory Profile ===
// Total allocated: 52428800 bytes (50.00 MB)
//
// Per-type breakdown:
//   LargeType: 41943040 bytes (40.00 MB), 100 ordinals
//   MediumType: 8388608 bytes (8.00 MB), 1000 ordinals
//   SmallType: 2097152 bytes (2.00 MB), 10000 ordinals
```

**Use cases:**
- Identify types consuming most memory
- Optimize serialization for large types
- Tune memory allocation strategies

## Limitations

**Not viable for:**

- Conflicting delta chains (multiple producers)
- Wrong dataset delivery (different data stream)
- Producer corrupt blobs (snapshot also corrupt)

**Version jumping limitations:**

- Requires snapshot available at target version
- Falls back to regular planning if snapshot unavailable
- Does not attempt delta path when using `planUpdateWithRepairJump()` explicitly

**Requires:**

- Producer publishes checksums in announcements
- Snapshots available for all versions
- Schema compatibility between delta and snapshot

## Usage Examples

### Version Jumping with Repair Transitions

Use repair transitions to jump between versions when delta chains are broken:

```java
HollowUpdatePlanner planner = new HollowUpdatePlanner(blobRetriever);

// Forward jump: v100 -> v200 (when delta chain broken at v101)
HollowUpdatePlan plan = planner.planUpdateWithRepairJump(100L, 200L);

// Reverse jump: v200 -> v100 (when reverse deltas unavailable)
HollowUpdatePlan rollbackPlan = planner.planUpdateWithRepairJump(200L, 100L);

// Apply the repair plan
if (plan != null && plan != HollowUpdatePlan.DO_NOTHING) {
    consumer.triggerRefreshTo(plan.getTransition(0).getToVersion());
}
```

**Benefits over snapshot fallback:**

- Explicit control over version jumping
- Uses REPAIR blob type for observability
- Triggers `repairApplied()` listener hooks instead of snapshot hooks
- Clear intent: version jump vs. corruption fix

### Producer Configuration

```java
HollowProducer producer = HollowProducer
    .withPublisher(blobPublisher)
    .withAnnouncer(announcer)
    .build();

// Checksums automatically computed and announced
producer.runCycle(state -> {
    // Populate state
    writeRecords(state);
});
```

Checksums are computed automatically after each publish cycle and included in announcement metadata under the key `hollow.checksum`.

### Consumer Configuration

#### Detection Only

Enable checksum validation to detect but not automatically repair integrity violations:

```java
HollowConsumer consumer = HollowConsumer
    .withBlobRetriever(retriever)
    .withChecksumValidation(true)
    .build();
```

With this configuration, checksum mismatches will be logged at WARNING level but no repair will occur. This is useful for monitoring and alerting without automatic remediation.

#### Automatic Repair

Enable both checksum validation and automatic repair:

```java
HollowConsumer consumer = HollowConsumer
    .withBlobRetriever(retriever)
    .withChecksumValidation(true)
    .withRepairEnabled(true)
    .build();
```

When a checksum mismatch is detected after delta application, the consumer will automatically trigger a repair transition using the snapshot as source of truth.  
