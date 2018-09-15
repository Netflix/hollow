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
import com.netflix.hollow.api.producer.HollowProducer.Validator;
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
 * Example construction - note that {@code Movie} and {@code MovieAPI} are generated classes:
 * <code>
 *     new ObjectModificationValidator&lt;MovieAPI, Movie&gt;(
 *         "Movie",
 *         (oldMovie, newMovie) -&gt; oldMovie.getName().equals(newMovie.getName()),
 *         MovieAPI::new,
 *         (api, ordinal) -&gt; api.getMovie(ordinal));
 * </code>
 */
public class ObjectModificationValidator<A extends HollowAPI, T extends HollowObject>
        implements Validator {
    private final String typeName;
    private final Function<HollowDataAccess, A> apiFunction;
    private final BiFunction<A, Integer, T> hollowObjectFunction;
    private final BiPredicate<T, T> filter;

    private SingleValidationStatus.SingleValidationStatusBuilder statusBuilder;

    @SuppressWarnings("WeakerAccess")
    public ObjectModificationValidator(String typeName, BiPredicate<T, T> filter,
            Function<HollowDataAccess, A> apiFunction,
            BiFunction<A, Integer, T> hollowObjectFunction) {
		this.typeName = typeName;
		this.filter = filter;
		this.apiFunction = apiFunction;
		this.hollowObjectFunction = hollowObjectFunction;
        this.statusBuilder = SingleValidationStatus.builder(ObjectModificationValidator.class.getSimpleName())
                .addAdditionalInfo("typeName", typeName);
    }

    @Override
	public void validate(ReadState readState) throws ValidationException {
        try {
            HollowTypeReadState typeState = readState.getStateEngine().getTypeState(typeName);
            if (typeState == null) {
                throw new ValidationException("Cannot execute ObjectModificationValidator on missing type "
                        + typeName);
            } else if (typeState.getSchema().getSchemaType() != SchemaType.OBJECT) {
                throw new ValidationException("Cannot execute ObjectModificationValidator on type " + typeName
                        + " because it is not a HollowObjectSchema - it is a "
                        + typeState.getSchema().getSchemaType());
            }
            BitSet latestOrdinals = typeState.getPopulatedOrdinals();
            BitSet previousOrdinals = typeState.getPreviousOrdinals();
            if (previousOrdinals.isEmpty()) {
                statusBuilder.withMessage(ObjectModificationValidator.class.getSimpleName()
                        + " has nothing to do because previous cycle has no " + typeName).success();
                return;
            }
            BitSet removedAndModified = calculateRemovedAndModifiedOrdinals(latestOrdinals, previousOrdinals);
            if (removedAndModified.isEmpty()) {
                statusBuilder.withMessage(ObjectModificationValidator.class.getSimpleName()
                        + " has nothing to do because " + typeName
                        + " has no removals or modifications").success();
                return;
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
                        throw new ValidationException("Validation failed for key " + Arrays.toString(candidateKey));
                    }
                }
                fromOrdinal = removedAndModified.nextSetBit(fromOrdinal + 1);
            }
            statusBuilder.success();
        } catch (RuntimeException e) {
            statusBuilder.withMessage(e.getMessage()).fail(e);
            throw e;
        }
    }

	@Override
	public String toString() {
        SingleValidationStatus status = statusBuilder.build();
        return getClass().getSimpleName() + "<typeName=" + typeName + ", status=" + status.getStatus()
                + ", message=" + status.getMessage() + ", additionalInfo=" + status.getAdditionalInfo()
                + ", exception=" + status.getThrowable() + ">";
    }

    private static BitSet calculateRemovedAndModifiedOrdinals(BitSet latestOrdinals, BitSet previousOrdinals) {
        // make sure we don't modify previousOrdinals or latestOrdinals
        BitSet removedAndModified = new BitSet();
        removedAndModified.or(previousOrdinals);
        removedAndModified.andNot(latestOrdinals);
        return removedAndModified;
    }
}
