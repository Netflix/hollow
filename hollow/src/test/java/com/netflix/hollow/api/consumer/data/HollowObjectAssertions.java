package com.netflix.hollow.api.consumer.data;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;

final class HollowObjectAssertions {
    private HollowObjectAssertions() {}

    static void assertObject(HollowObjectTypeReadState readState, int ordinal, int intVal, String strVal) {
        GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(readState), ordinal);

        assertEquals(intVal, obj.getInt("a1"));
        assertEquals(strVal, obj.getString("a2"));
    }

    static <T extends HollowObject> void assertList(Collection<T> listOfObj, List<Integer> listOfIds) {
        int i = 0;
        for (T obj : listOfObj) {
            int id = listOfIds.get(i++);
            assertEquals(id, obj.getInt("a1"));
        }
    }

    static <T extends HollowObject> void assertUpdatedList(
            Collection<AbstractHollowDataAccessor.UpdatedRecord<T>> listOfObj, List<String> beforeValues,
            List<String> afterValues) {
        int i = 0;
        for (AbstractHollowDataAccessor.UpdatedRecord<T> obj : listOfObj) {
            int beforeId = obj.getBefore().getInt("a1");
            int afterId = obj.getAfter().getInt("a1");
            assertEquals(beforeId, afterId);

            String beforeVal = beforeValues.get(i);
            String afterVal = afterValues.get(i++);
            Assert.assertNotEquals(beforeVal, afterVal);
            assertEquals(beforeVal, obj.getBefore().getString("a2"));
            assertEquals(afterVal, obj.getAfter().getString("a2"));
        }
    }
}
