# UUID_LONG Migration Plan: Dual-Read Strategy

**Document Owner:** Engineering Team
**Last Updated:** January 2025
**Status:** Draft
**Target Completion:** 3-4 weeks from start

---

## Executive Summary

This document outlines a zero-downtime migration strategy to transition Hollow's UUID field type from `FieldType.LONG` to the optimized `FieldType.UUID_LONG`. The migration uses a dual-read approach that ensures backward compatibility throughout the transition period.

**Key Benefits:**
- Zero downtime during migration
- No data loss or corruption risk
- Rollback capability at each phase
- Improved performance post-migration (reduced memory allocation for UUID fields)

---

## Table of Contents

1. [Background](#background)
2. [Migration Overview](#migration-overview)
3. [Technical Implementation](#technical-implementation)
4. [Rollout Timeline](#rollout-timeline)
5. [Rollback Procedures](#rollback-procedures)
6. [Testing & Validation](#testing--validation)
7. [Monitoring & Alerts](#monitoring--alerts)
8. [Post-Migration Cleanup](#post-migration-cleanup)

---

## Background

### Current State

As of commit `d3b93801b`, Hollow supports UUID serialization using two `FieldType.LONG` fields:
- `mostSigBits: LONG`
- `leastSigBits: LONG`

### Problem Statement

The current implementation has a performance limitation:
- UUID fields require the full 64-bit range for each component
- Standard `LONG` fields use zig-zag encoding for compression
- When UUIDs are present, they force all LONG fields in the schema to allocate 64 bits, reducing compression efficiency for other long values

### Proposed Solution

Introduce `FieldType.UUID_LONG` (commit `632fc9b69`):
- Dedicated field type for UUID components
- Fixed 64-bit encoding without zig-zag
- Prevents UUID values from affecting regular LONG field compression

### Challenge

Schema incompatibility:
- Old schema: `mostSigBits: LONG`
- New schema: `mostSigBits: UUID_LONG`
- Hollow's schema validation throws `IncompatibleSchemaException` when field types don't match

---

## Migration Overview

### Three-Phase Approach

```
Phase 1: Dual-Read Consumers    Phase 2: UUID_LONG Writers    Phase 3: Cleanup
        (Week 1)                       (Week 2)                  (Week 3+)

┌─────────────────────┐        ┌─────────────────────┐      ┌─────────────────────┐
│ Deploy consumers    │        │ Deploy producers    │      │ Remove compatibility│
│ that accept both    │   →    │ writing UUID_LONG   │  →   │ layer               │
│ LONG and UUID_LONG  │        │                     │      │                     │
└─────────────────────┘        └─────────────────────┘      └─────────────────────┘

Old Consumers: ✗               Old Consumers: ✗             Old Consumers: ✗
Phase 1 Consumers: ✓           Phase 1 Consumers: ✓         Phase 3 Consumers: ✓

Old Producers: ✓               New Producers: ✓              New Producers: ✓
                               Old Producers: ✓ (allowed)
```

### Success Criteria

- ✓ No service downtime
- ✓ No schema validation errors in production
- ✓ All consumers can read UUID_LONG format
- ✓ All producers write UUID_LONG format
- ✓ Performance improvement verified (reduced memory usage)

---

## Technical Implementation

### Phase 1: Dual-Read Consumer Support

#### 1.1 Schema Compatibility Layer

**File:** `hollow/src/main/java/com/netflix/hollow/core/schema/HollowObjectSchema.java`

**Location:** Add new method around line 160

```java
/**
 * Checks if two field types are compatible for schema evolution.
 * During the UUID_LONG migration, LONG and UUID_LONG are considered compatible.
 *
 * @param type1 First field type
 * @param type2 Second field type
 * @return true if types are compatible
 */
private boolean areFieldTypesCompatible(FieldType type1, FieldType type2) {
    if (type1 == type2) {
        return true;
    }

    // UUID_LONG migration compatibility:
    // Allow LONG and UUID_LONG to be considered compatible
    // TODO: Remove this after UUID_LONG migration completes (target: 2025-Q1)
    if ((type1 == FieldType.LONG && type2 == FieldType.UUID_LONG) ||
        (type1 == FieldType.UUID_LONG && type2 == FieldType.LONG)) {
        return true;
    }

    return false;
}
```

**Location:** Modify `findCommonSchema()` method around line 179

```java
// BEFORE:
if (fieldTypes[i] != otherSchema.getFieldType(otherFieldIndex)
        || !referencedTypesEqual(referencedTypes[i], otherSchema.getReferencedType(otherFieldIndex))) {
    throw new IncompatibleSchemaException(getName(), fieldNames[i], fieldType, otherFieldType);
}

// AFTER:
if (!areFieldTypesCompatible(fieldTypes[i], otherSchema.getFieldType(otherFieldIndex))
        || !referencedTypesEqual(referencedTypes[i], otherSchema.getReferencedType(otherFieldIndex))) {
    String fieldType = fieldTypes[i] == FieldType.REFERENCE ? referencedTypes[i]
        : fieldTypes[i].toString().toLowerCase();
    String otherFieldType = otherSchema.getFieldType(otherFieldIndex) == FieldType.REFERENCE
        ? otherSchema.getReferencedType(otherFieldIndex)
        : otherSchema.getFieldType(otherFieldIndex).toString().toLowerCase();
    throw new IncompatibleSchemaException(getName(), fieldNames[i], fieldType, otherFieldType);
}
```

#### 1.2 Read State Compatibility

**File:** `hollow/src/main/java/com/netflix/hollow/core/read/engine/object/HollowObjectTypeReadState.java`

**Location:** Modify `readLong()` method around line 290

```java
public long readLong(int ordinal, int fieldIndex) {
    HollowObjectSchema.FieldType fieldType = getSchema().getFieldType(fieldIndex);

    // Handle UUID_LONG fields specially (no zig-zag decoding)
    if (fieldType == HollowObjectSchema.FieldType.UUID_LONG) {
        HollowObjectTypeDataElements shard;
        long value;
        do {
            HollowObjectTypeDataElements[] shardsArray = shardsVolatile;
            shard = shardsArray[ordinal & shardsArray[0].shardMask];
            value = shard.readLong(ordinal >> shard.shardOrdinalShift, fieldIndex);
        } while(readWasUnsafe(shardsHolder, ordinal, shard));

        // UUID_LONG: return raw value without zig-zag decoding
        if(value == shard.dataElements.nullValueForField[fieldIndex])
            return Long.MIN_VALUE;
        return value;
    }

    // Regular LONG: apply zig-zag decoding
    // ... existing implementation
}
```

#### 1.3 Flat Record Reader Compatibility

**File:** `hollow/src/main/java/com/netflix/hollow/core/write/objectmapper/flatrecords/FlatRecordOrdinalReader.java`

**Location:** Add method around line 176

```java
public long readFieldLongOrUuidLong(int ordinal, String field) {
    HollowObjectSchema.FieldType fieldType = skipToFieldType(ordinal, field);

    if (fieldType == null) {
        return Long.MIN_VALUE;
    }

    if (fieldType == HollowObjectSchema.FieldType.UUID_LONG) {
        return readFieldUuidLong(ordinal, field);
    } else if (fieldType == HollowObjectSchema.FieldType.LONG) {
        return readFieldLong(ordinal, field);
    }

    throw new IllegalStateException("Expected LONG or UUID_LONG field type, got: " + fieldType);
}
```

#### 1.4 Update Type Mapper Reader

**File:** `hollow/src/main/java/com/netflix/hollow/core/write/objectmapper/HollowObjectTypeMapper.java`

**Location:** Modify UUID reading in `parseHollowRecord()` around line 268

```java
if (clazz == UUID.class) {
    Long mostSigBits = null;
    Long leastSigBits = null;

    int mostSigBitsPosInPojoSchema = schema.getPosition(MappedFieldType.UUID_MOST_SIG_BITS.getSpecialFieldName());
    if(mostSigBitsPosInPojoSchema != -1) {
        // Support both LONG and UUID_LONG during migration
        HollowObjectSchema.FieldType fieldType =
            ((HollowObjectSchema)hollowObject.getSchema()).getFieldType(mostSigBitsPosInPojoSchema);

        long value;
        if (fieldType == HollowObjectSchema.FieldType.UUID_LONG) {
            value = hollowObject.getTypeDataAccess().readLong(
                hollowObject.getOrdinal(), mostSigBitsPosInPojoSchema);
        } else {
            // Fallback to regular LONG reading
            value = hollowObject.getLong(MappedFieldType.UUID_MOST_SIG_BITS.getSpecialFieldName());
        }

        if(value != Long.MIN_VALUE) {
            mostSigBits = value;
        }
    }

    // Similar logic for leastSigBits
    int leastSigBitsPosInPojoSchema = schema.getPosition(MappedFieldType.UUID_LEAST_SIG_BITS.getSpecialFieldName());
    if(leastSigBitsPosInPojoSchema != -1) {
        HollowObjectSchema.FieldType fieldType =
            ((HollowObjectSchema)hollowObject.getSchema()).getFieldType(leastSigBitsPosInPojoSchema);

        long value;
        if (fieldType == HollowObjectSchema.FieldType.UUID_LONG) {
            value = hollowObject.getTypeDataAccess().readLong(
                hollowObject.getOrdinal(), leastSigBitsPosInPojoSchema);
        } else {
            value = hollowObject.getLong(MappedFieldType.UUID_LEAST_SIG_BITS.getSpecialFieldName());
        }

        if(value != Long.MIN_VALUE) {
            leastSigBits = value;
        }
    }

    if (mostSigBits != null && leastSigBits != null) {
        obj = new UUID(mostSigBits, leastSigBits);
    }
}
```

### Phase 2: UUID_LONG Writer Deployment

**No code changes required** - this phase only involves deploying the code from commit `632fc9b69` where:

```java
UUID_MOST_SIG_BITS(FieldType.UUID_LONG, "mostSigBits"),
UUID_LEAST_SIG_BITS(FieldType.UUID_LONG, "leastSigBits"),
```

Phase 1 consumers will already be able to read this format.

### Phase 3: Remove Compatibility Layer

After all services are upgraded, remove the temporary compatibility code:

1. Remove `areFieldTypesCompatible()` method
2. Restore original field type checking in `findCommonSchema()`
3. Remove dual-read logic from type mappers
4. Update documentation to reflect UUID_LONG as the standard

---

## Rollout Timeline

### Week 1: Phase 1 - Dual-Read Consumer Deployment

| Day | Activity | Validation |
|-----|----------|------------|
| **Mon** | Deploy Phase 1 code to canary consumers (5% traffic) | Monitor for schema errors, no `IncompatibleSchemaException` |
| **Tue** | Monitor canary metrics, check memory/CPU | Compare with baseline, ensure no performance degradation |
| **Wed** | Deploy to 25% of consumer fleet | Monitor error rates, schema validation success |
| **Thu** | Deploy to 50% of consumer fleet | Continue monitoring |
| **Fri** | Deploy to 100% of consumer fleet | All consumers now support both formats |

**Go/No-Go Criteria:**
- ✓ Zero `IncompatibleSchemaException` errors
- ✓ Consumer lag remains within normal range
- ✓ No increase in memory/CPU usage
- ✓ Test data validates correctly

### Week 2: Phase 2 - UUID_LONG Producer Deployment

| Day | Activity | Validation |
|-----|----------|------------|
| **Mon** | Wait period - ensure all consumers from Week 1 are stable | Review monitoring dashboards |
| **Tue** | Deploy UUID_LONG producers to canary (5% traffic) | Verify consumers can read new format |
| **Wed** | Deploy to 25% of producer fleet | Monitor consumer error rates |
| **Thu** | Deploy to 50% of producer fleet | Check data integrity |
| **Fri** | Deploy to 100% of producer fleet | All data now in UUID_LONG format |

**Go/No-Go Criteria:**
- ✓ Consumers successfully read UUID_LONG data
- ✓ No data corruption or parsing errors
- ✓ Performance improvement visible (memory usage reduction)
- ✓ Snapshot sizes reduced (verify compression improvement)

### Week 3+: Phase 3 - Cleanup

| Timeframe | Activity | Validation |
|-----------|----------|------------|
| **Week 3** | Monitor production with UUID_LONG | Collect metrics, verify stability |
| **Week 4** | Create PR to remove compatibility layer | Code review, testing |
| **Week 5** | Deploy cleanup to canary | Ensure no regressions |
| **Week 6** | Full deployment of cleanup | Migration complete |

**Optional:** Keep compatibility layer for an additional 2-4 weeks for extra safety margin.

---

## Rollback Procedures

### Rollback During Phase 1 (Week 1)

**Scenario:** Issues detected with dual-read consumer code

**Steps:**
1. Identify affected consumer services
2. Revert to previous version without compatibility layer
3. All producers are still writing LONG format, so no data issues
4. **Impact:** Low - only affects services in rollout

**Rollback Time:** < 1 hour

### Rollback During Phase 2 (Week 2)

**Scenario:** Issues detected with UUID_LONG producer code

**Steps:**
1. Stop rollout of UUID_LONG producers
2. Revert affected producers to LONG format
3. Phase 1 consumers can still read LONG format
4. Investigate issue, fix, and restart Phase 2
5. **Impact:** Low - dual-read consumers handle both formats

**Rollback Time:** < 2 hours

### Emergency Rollback After Phase 2

**Scenario:** Critical issue discovered after full UUID_LONG deployment

**Steps:**
1. **DO NOT** rollback producers immediately (would cause schema mismatch)
2. Keep compatibility layer in place (extend Phase 3 timeline)
3. If necessary, roll back producers first, then consumers
4. Requires coordinated rollback across all services
5. **Impact:** Medium - requires careful orchestration

**Rollback Time:** 4-8 hours (requires full fleet rollback)

**Prevention:** Thorough testing in Phase 1 and Phase 2 canary deployments

---

## Testing & Validation

### Unit Tests

Create comprehensive unit tests covering:

```java
@Test
public void testSchemaCompatibility_LongAndUuidLong() {
    // Test that schemas with LONG and UUID_LONG are compatible
    HollowObjectSchema schema1 = new HollowObjectSchema("UUID", 2);
    schema1.addField("mostSigBits", FieldType.LONG);
    schema1.addField("leastSigBits", FieldType.LONG);

    HollowObjectSchema schema2 = new HollowObjectSchema("UUID", 2);
    schema2.addField("mostSigBits", FieldType.UUID_LONG);
    schema2.addField("leastSigBits", FieldType.UUID_LONG);

    // Should not throw IncompatibleSchemaException
    HollowObjectSchema commonSchema = schema1.findCommonSchema(schema2);
    assertNotNull(commonSchema);
}

@Test
public void testReadUuid_BothFormats() {
    UUID testUuid = UUID.randomUUID();

    // Test reading UUID written as LONG
    HollowWriteStateEngine writeStateOld = new HollowWriteStateEngine();
    HollowObjectMapper mapperOld = new HollowObjectMapper(writeStateOld);
    // ... configure to use LONG
    int ordinal1 = mapperOld.add(testUuid);

    // Test reading UUID written as UUID_LONG
    HollowWriteStateEngine writeStateNew = new HollowWriteStateEngine();
    HollowObjectMapper mapperNew = new HollowObjectMapper(writeStateNew);
    // ... configure to use UUID_LONG
    int ordinal2 = mapperNew.add(testUuid);

    // Both should deserialize to the same UUID
    assertEquals(testUuid, readBack1);
    assertEquals(testUuid, readBack2);
}

@Test
public void testUuidLongEncoding_NoZigZag() {
    // Verify UUID_LONG doesn't use zig-zag encoding
    long rawValue = -1L; // All bits set

    // UUID_LONG should store raw value
    // Regular LONG would apply zig-zag

    // ... test implementation
}
```

### Integration Tests

**Test Scenarios:**

1. **Cross-Version Compatibility**
   - Producer with LONG → Consumer with dual-read
   - Producer with UUID_LONG → Consumer with dual-read
   - Producer with UUID_LONG → Consumer with UUID_LONG only

2. **Data Integrity**
   - Write 10,000 random UUIDs
   - Read back and verify all match
   - Test with null UUIDs
   - Test with special UUID values (all zeros, all ones, etc.)

3. **Schema Evolution**
   - Create snapshot with LONG format
   - Read with UUID_LONG consumers
   - Verify no data loss

4. **Performance Benchmarks**
   - Measure memory usage: LONG vs UUID_LONG
   - Measure serialization time
   - Measure deserialization time
   - Measure snapshot size

### Staging Environment Tests

Before production rollout:

1. Deploy Phase 1 to staging for 48 hours
2. Run load tests with production-like traffic
3. Deploy Phase 2 to staging for 48 hours
4. Verify metrics match expectations
5. Perform chaos testing (random service restarts)

---

## Monitoring & Alerts

### Key Metrics to Monitor

#### Schema Validation Metrics

```
metric: hollow.schema.validation.errors
alert: > 0 errors per minute
action: Halt rollout, investigate immediately
```

#### Consumer Lag Metrics

```
metric: hollow.consumer.lag.seconds
alert: > 2x baseline lag
action: Review consumer performance, consider rollback
```

#### Memory Usage

```
metric: hollow.consumer.memory.heap.used
alert: > 20% increase from baseline
action: Investigate memory leak, review compatibility layer
```

#### Read Errors

```
metric: hollow.read.errors.uuid_deserialization
alert: > 0 errors per minute
action: Halt rollout, check data corruption
```

### Dashboards

Create monitoring dashboards with:

1. **Schema Compatibility Dashboard**
   - Count of LONG format reads
   - Count of UUID_LONG format reads
   - Schema validation errors
   - Compatibility layer invocations

2. **Performance Dashboard**
   - Memory usage trends
   - Deserialization latency (p50, p99)
   - Snapshot size trends
   - CPU usage

3. **Migration Progress Dashboard**
   - % of consumers on Phase 1 code
   - % of producers writing UUID_LONG
   - % of data in UUID_LONG format

### Alert Thresholds

| Alert | Severity | Threshold | Action |
|-------|----------|-----------|--------|
| Schema validation errors | **Critical** | > 0 | Halt rollout immediately |
| Consumer lag spike | **High** | > 5 minutes | Investigate, consider pause |
| Memory increase | **Medium** | > 20% | Monitor, may continue |
| Deserialization errors | **Critical** | > 0 | Halt rollout immediately |
| CPU increase | **Low** | > 30% | Monitor, usually temporary |

---

## Post-Migration Cleanup

### Code Cleanup (Week 5-6)

Remove temporary compatibility code:

**Files to Update:**
- `HollowObjectSchema.java` - Remove `areFieldTypesCompatible()`
- `HollowObjectTypeReadState.java` - Remove dual-format read logic
- `HollowObjectTypeMapper.java` - Simplify UUID reading
- `FlatRecordOrdinalReader.java` - Remove `readFieldLongOrUuidLong()`

**Create PR with:**
- Clear description: "Remove UUID_LONG migration compatibility layer"
- Reference this migration document
- Before/after performance metrics
- Confirmation all services are upgraded

### Documentation Updates

1. **Update Hollow Schema Documentation**
   - Document `FieldType.UUID_LONG`
   - Explain when to use vs `FieldType.LONG`
   - Add code examples

2. **Update Migration Guide**
   - Document this migration as a case study
   - Provide template for future field type migrations
   - Lessons learned

3. **Update API Generator**
   - Ensure generated code uses UUID_LONG for UUID fields
   - Update code generation templates
   - Add tests for UUID field generation

### Performance Validation

**Measure and document improvements:**

Expected improvements:
- Memory usage: 10-15% reduction for datasets with UUIDs
- Snapshot size: 5-10% reduction (depends on UUID prevalence)
- Compression: Better compression for regular LONG fields

**Create report with:**
- Before/after metrics from production
- Memory savings calculations
- Performance benchmarks
- ROI analysis (engineering time vs savings)

### Knowledge Sharing

1. **Write postmortem/retrospective**
   - What went well
   - What could be improved
   - Unexpected issues encountered
   - Recommendations for future migrations

2. **Team presentation**
   - Share migration strategy with broader team
   - Discuss technical implementation
   - Review monitoring approach

3. **Update runbooks**
   - Document UUID_LONG behavior
   - Add troubleshooting guides
   - Update operational procedures

---

## Success Metrics

### Technical Success

- ✅ Zero production incidents related to migration
- ✅ Zero data loss or corruption
- ✅ All services upgraded within 3-week timeline
- ✅ Compatibility layer removed successfully
- ✅ All tests passing post-migration

### Performance Success

- ✅ Memory usage reduced by 10-15% for UUID-heavy datasets
- ✅ Snapshot sizes reduced by 5-10%
- ✅ Regular LONG field compression improved
- ✅ No increase in deserialization latency

### Operational Success

- ✅ Clear documentation for future migrations
- ✅ Team confidence in schema evolution
- ✅ Monitoring and alerting validated
- ✅ Rollback procedures tested

---

## FAQ

### Q: What happens if a service doesn't upgrade during Week 1?

**A:** Old consumers that haven't upgraded to Phase 1 code will encounter `IncompatibleSchemaException` errors once producers start writing UUID_LONG in Week 2. This is why it's critical to ensure **all consumers** are upgraded before proceeding to Phase 2.

**Mitigation:**
- Maintain a service registry to track which services consume Hollow data
- Send notifications to service owners before each phase
- Implement automated checks to verify all consumers are upgraded

### Q: Can we speed up the migration timeline?

**A:** The 3-week timeline includes safety margins. If you have:
- High confidence in test coverage
- Ability to quickly rollback
- Good monitoring in place
- Small number of services to coordinate

You could compress to 2 weeks (5 days per phase), but this increases risk.

### Q: What if we discover a bug after Phase 3 cleanup?

**A:** If a critical bug is found after the compatibility layer is removed:
1. **Immediate:** Roll back to version with compatibility layer
2. **Short-term:** Fix the bug, test thoroughly
3. **Long-term:** Redeploy fixed version

This is why we recommend keeping compatibility layer for 3-4 weeks minimum.

### Q: How do we handle third-party consumers?

**A:** If external teams consume your Hollow data:
1. Provide **6-8 weeks notice** before starting migration
2. Share this migration plan document
3. Offer Phase 1 code as a library update
4. Provide support during their upgrade process
5. Consider extended compatibility period (8-12 weeks)

### Q: What's the disk space savings?

**A:** Savings depend on your data:
- **High UUID density** (e.g., 50% of fields are UUIDs): 10-15% reduction
- **Medium UUID density** (e.g., 10-20% UUID fields): 5-10% reduction
- **Low UUID density** (e.g., <5% UUID fields): 2-5% reduction

Measure your production data to estimate savings.

### Q: Can we migrate only some data models?

**A:** No, the migration must be applied uniformly:
- All UUID fields use the same field type
- Mixing LONG and UUID_LONG for different UUIDs would be confusing
- The compatibility layer handles the transition globally

---

## Appendix A: Code Review Checklist

Before merging Phase 1 code:

- [ ] Unit tests added for schema compatibility
- [ ] Integration tests cover cross-version scenarios
- [ ] Performance tests show no regression
- [ ] Code includes TODO comments with cleanup target date
- [ ] Documentation updated
- [ ] Monitoring dashboards created
- [ ] Alert thresholds configured
- [ ] Rollback procedures documented and tested
- [ ] Service owners notified
- [ ] Staging deployment successful

---

## Appendix B: Communication Template

**Email Subject:** [Action Required] Hollow UUID Migration - Week 1 Deployment

**Body:**

```
Hi Team,

We're beginning a 3-week migration to optimize UUID field storage in Hollow.

WHAT'S CHANGING:
- UUID fields will use a new optimized field type (UUID_LONG)
- Expected 10-15% memory reduction for UUID-heavy data

WHAT YOU NEED TO DO:
- Week 1 (Starting [DATE]): Deploy Phase 1 code to your consumers
  - Deployment link: [LINK]
  - No configuration changes needed

TIMELINE:
- Week 1: Consumer upgrades (that's you!)
- Week 2: Producer upgrades (platform team)
- Week 3+: Cleanup and validation

WHAT TO WATCH:
- Monitor your consumer lag dashboard
- Watch for schema validation errors
- Contact [TEAM] if you see any issues

QUESTIONS:
- Read the full migration plan: [LINK TO THIS DOCUMENT]
- Slack channel: #hollow-uuid-migration
- On-call: [CONTACT]

Thank you!
[Your Name]
```

---

## Appendix C: Glossary

| Term | Definition |
|------|------------|
| **FieldType.LONG** | Standard long integer field type with zig-zag encoding for compression |
| **FieldType.UUID_LONG** | Specialized field type for UUID components with fixed 64-bit encoding |
| **Zig-zag encoding** | Compression technique that maps signed integers to unsigned for better variable-length encoding |
| **Schema compatibility** | Ability for different schema versions to interoperate without errors |
| **Dual-read** | Code that can read data in multiple formats during a migration period |
| **Canary deployment** | Gradual rollout starting with a small percentage of traffic |
| **IncompatibleSchemaException** | Error thrown when schemas cannot be reconciled |

---

## Document Revision History

| Date | Version | Author | Changes |
|------|---------|--------|---------|
| 2025-01-14 | 1.0 | Engineering Team | Initial draft |
| | | | |
| | | | |

---

**Questions or concerns?** Contact the Hollow team at [contact info]

**Document Status:** ✅ Ready for Review