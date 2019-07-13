package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.UniqueKeyIndex;
import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class PersonVideo extends HollowObject {

    public PersonVideo(PersonVideoDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public PersonVideoAliasIdsList getAliasIds() {
        int refOrdinal = delegate().getAliasIdsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonVideoAliasIdsList(refOrdinal);
    }

    public PersonVideoRolesList getRoles() {
        int refOrdinal = delegate().getRolesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getPersonVideoRolesList(refOrdinal);
    }

    public long getPersonId() {
        return delegate().getPersonId(ordinal);
    }

    public Long getPersonIdBoxed() {
        return delegate().getPersonIdBoxed(ordinal);
    }

    public PersonVideoAPI api() {
        return typeApi().getAPI();
    }

    public PersonVideoTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PersonVideoDelegate delegate() {
        return (PersonVideoDelegate)delegate;
    }

    /**
     * Creates a unique key index for {@code PersonVideo} that has a primary key.
     * The primary key is represented by the type {@code long}.
     * <p>
     * By default the unique key index will not track updates to the {@code consumer} and thus
     * any changes will not be reflected in matched results.  To track updates the index must be
     * {@link HollowConsumer#addRefreshListener(HollowConsumer.RefreshListener) registered}
     * with the {@code consumer}
     *
     * @param consumer the consumer
     * @return the unique key index
     */
    public static UniqueKeyIndex<PersonVideo, Long> uniqueIndex(HollowConsumer consumer) {
        return UniqueKeyIndex.from(consumer, PersonVideo.class)
            .bindToPrimaryKey()
            .usingPath("personId", long.class);
    }

}