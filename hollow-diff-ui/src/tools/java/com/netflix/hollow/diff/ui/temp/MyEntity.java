package com.netflix.hollow.diff.ui.temp;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.FieldPath;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MyEntity extends HollowObject {

    public MyEntity(MyEntityDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public Integer getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
    }

    public int getId() {
        return delegate().getId(ordinal);
    }

    public HInteger getIdHollowReference() {
        int refOrdinal = delegate().getIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHInteger(refOrdinal);
    }

    public String getName() {
        return delegate().getName(ordinal);
    }

    public boolean isNameEqual(String testValue) {
        return delegate().isNameEqual(ordinal, testValue);
    }

    public HString getNameHollowReference() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public Integer getProfileIdBoxed() {
        return delegate().getProfileIdBoxed(ordinal);
    }

    public int getProfileId() {
        return delegate().getProfileId(ordinal);
    }

    public ProfileId getProfileIdHollowReference() {
        int refOrdinal = delegate().getProfileIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getProfileId(refOrdinal);
    }

    public MyNamespaceAPI api() {
        return typeApi().getAPI();
    }

    public MyEntityTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MyEntityDelegate delegate() {
        return (MyEntityDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code MyEntity} that has a primary key.
     * The primary key is represented by the class {@link MyEntity.Key}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<MyEntity, MyEntity.Key> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, MyEntity.class)
            .bindToPrimaryKey()
            .usingBean(MyEntity.Key.class);
    }

    public static class Key {
        @FieldPath("id.value")
        public final int idValue;

        @FieldPath("name.value")
        public final String nameValue;

        @FieldPath("profileId.value")
        public final int profileIdValue;

        public Key(int idValue, String nameValue, int profileIdValue) {
            this.idValue = idValue;
            this.nameValue = nameValue;
            this.profileIdValue = profileIdValue;
        }
    }

}