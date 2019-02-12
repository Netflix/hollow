/*
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.producer.validation;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowSchema.SchemaType;
import java.util.Arrays;
import java.util.BitSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * This validator runs a provided {@link BiPredicate} on every instance of an object that was
 * modified in a cycle. This "filter" function is called with the old object as the first argument
 * and the new object as the second argument. If the function returns false, this validator is
 * marked as failed and the producer cycle fails.
 * Note that the filter function is not called with any objects that were added or removed.
 * Also note that this validator can only be used on types that have a primary key.
 *
 * @apiNote Example construction - note that {@code Movie} and {@code MovieAPI} are generated classes:
 * <pre>{@code
 *     new ObjectModificationValidator<MovieAPI, Movie>(
 *         "Movie",
 *         (oldMovie, newMovie) -> oldMovie.getName().equals(newMovie.getName()),
 *         MovieAPI::new,
 *         (api, ordinal) -> api.getMovie(ordinal));
 * }</pre>
 */
public class ObjectModificationValidator<A extends HollowAPI, T extends HollowObject>
        implements ValidatorListener {
    private final String typeName;
    private final Function<HollowDataAccess, A> apiFunction;
    private final BiFunction<A, Integer, T> hollowObjectFunction;
    private final BiPredicate<T, T> filter;

    @SuppressWarnings("WeakerAccess")
    public ObjectModificationValidator(
            String typeName, BiPredicate<T, T> filter,
            Function<HollowDataAccess, A> apiFunction,
            BiFunction<A, Integer, T> hollowObjectFunction) {
        this.typeName = typeName;
        this.filter = filter;
        this.apiFunction = apiFunction;
        this.hollowObjectFunction = hollowObjectFunction;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + "_" + typeName;
    }

    @Override
    public ValidationResult onValidate(ReadState readState) {
        ValidationResult.ValidationResultBuilder vrb = ValidationResult.from(this)
                .detail("Typename", typeName);

        HollowTypeReadState typeState = readState.getStateEngine().getTypeState(typeName);
        if (typeState == null) {
            return vrb.failed("Cannot execute ObjectModificationValidator on missing type " + typeName);
        } else if (typeState.getSchema().getSchemaType() != SchemaType.OBJECT) {
            return vrb.failed("Cannot execute ObjectModificationValidator on type " + typeName
                    + " because it is not a HollowObjectSchema - it is a "
                    + typeState.getSchema().getSchemaType());
        }

        BitSet latestOrdinals = typeState.getPopulatedOrdinals();
        BitSet previousOrdinals = typeState.getPreviousOrdinals();
        if (previousOrdinals.isEmpty()) {
            return vrb.detail("skipped", Boolean.TRUE)
                    .passed("Nothing to do because previous cycle has no " + typeName);
        }

        BitSet removedAndModified = calculateRemovedAndModifiedOrdinals(latestOrdinals, previousOrdinals);
        if (removedAndModified.isEmpty()) {
            return vrb.detail("skipped", Boolean.TRUE)
                    .passed("Nothing to do because " + typeName + " has no removals or modifications");
        }

        A hollowApi = apiFunction.apply(readState.getStateEngine());
        HollowObjectTypeReadState objectTypeState = (HollowObjectTypeReadState) typeState;
        PrimaryKey key = objectTypeState.getSchema().getPrimaryKey();
        // this is guaranteed to give us items from the most recent cycle, not the last one
        HollowPrimaryKeyIndex index = new HollowPrimaryKeyIndex(readState.getStateEngine(), key);
        int fromOrdinal = removedAndModified.nextSetBit(0);
        while (fromOrdinal != HollowConstants.ORDINAL_NONE) {
            Object[] candidateKey = index.getRecordKey(fromOrdinal);
            int matchedOrdinal = index.getMatchingOrdinal(candidateKey);
            if (matchedOrdinal != HollowConstants.ORDINAL_NONE) {
                T fromObject = hollowObjectFunction.apply(hollowApi, fromOrdinal);
                T toObject = hollowObjectFunction.apply(hollowApi, matchedOrdinal);
                if (!filter.test(fromObject, toObject)) {
                    return vrb.detail("candidateKey", Arrays.toString(candidateKey))
                            .detail("fromOrdinal", fromOrdinal)
                            .detail("toOrdinal", matchedOrdinal)
                            .failed("Validation failed for candidate key");
                }
            }
            fromOrdinal = removedAndModified.nextSetBit(fromOrdinal + 1);
        }

        return vrb.passed();
    }

    private static BitSet calculateRemovedAndModifiedOrdinals(BitSet latestOrdinals, BitSet previousOrdinals) {
        // make sure we don't modify previousOrdinals or latestOrdinals
        BitSet removedAndModified = new BitSet();
        removedAndModified.or(previousOrdinals);
        removedAndModified.andNot(latestOrdinals);
        return removedAndModified;
    }
}
