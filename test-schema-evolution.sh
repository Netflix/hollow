#!/bin/bash
# Manual verification of schema evolution feature

echo "Testing true schema evolution with Delta Schema Append..."
echo ""

# Test schema evolution with added field
echo "1. Testing schema evolution with added field..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testSchemaEvolutionWithAddedField" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Schema evolution with added field test passed"
else
    echo "   ✗ Schema evolution with added field test failed"
    exit 1
fi

# Test schema evolution with multiple added fields
echo "2. Testing schema evolution with multiple added fields..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testSchemaEvolutionWithMultipleAddedFields" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Schema evolution with multiple added fields test passed"
else
    echo "   ✗ Schema evolution with multiple added fields test failed"
    exit 1
fi

# Test schema evolution with field reordering
echo "3. Testing schema evolution with field reordering..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testSchemaEvolutionWithFieldReordering" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Schema evolution with field reordering test passed"
else
    echo "   ✗ Schema evolution with field reordering test failed"
    exit 1
fi

# Test backwards compatibility
echo "4. Testing backwards compatibility (old consumer with new producer)..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testBackwardsCompatibilityConsumerWithoutEvolvedSchema" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Backwards compatibility test passed"
else
    echo "   ✗ Backwards compatibility test failed"
    exit 1
fi

# Test forward compatibility
echo "5. Testing forward compatibility (evolved consumer reads new fields)..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testForwardCompatibilityEvolvedConsumerReadsNewFields" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Forward compatibility test passed"
else
    echo "   ✗ Forward compatibility test failed"
    exit 1
fi

# Test multi-delta schema evolution
echo "6. Testing multi-delta schema evolution..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testMultiDeltaSchemaEvolution" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Multi-delta schema evolution test passed"
else
    echo "   ✗ Multi-delta schema evolution test failed"
    exit 1
fi

# Test randomized schema evolution
echo "7. Testing randomized schema evolution..."
./gradlew :hollow:test --tests "com.netflix.hollow.core.write.DeltaSchemaAppendCorrectnessTest.testRandomizedSchemaEvolution" -q

if [ $? -eq 0 ]; then
    echo "   ✓ Randomized schema evolution test passed"
else
    echo "   ✗ Randomized schema evolution test failed"
    exit 1
fi

echo ""
echo "✓ All schema evolution tests passed!"
echo ""
echo "Summary:"
echo "  - Schema evolution with single field addition: PASSED"
echo "  - Schema evolution with multiple field additions: PASSED"
echo "  - Schema evolution with field reordering: PASSED"
echo "  - Backwards compatibility (old consumer): PASSED"
echo "  - Forward compatibility (evolved consumer): PASSED"
echo "  - Multi-delta schema evolution: PASSED"
echo "  - Randomized schema evolution: PASSED"
