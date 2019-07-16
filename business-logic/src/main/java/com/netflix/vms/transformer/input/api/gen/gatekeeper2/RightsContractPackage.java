package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPackage extends HollowObject {

    public RightsContractPackage(RightsContractPackageDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getPackageId() {
        return delegate().getPackageId(ordinal);
    }

    public Long getPackageIdBoxed() {
        return delegate().getPackageIdBoxed(ordinal);
    }

    public boolean getPrimary() {
        return delegate().getPrimary(ordinal);
    }

    public Boolean getPrimaryBoxed() {
        return delegate().getPrimaryBoxed(ordinal);
    }

    public boolean getHasRequiredStreams() {
        return delegate().getHasRequiredStreams(ordinal);
    }

    public Boolean getHasRequiredStreamsBoxed() {
        return delegate().getHasRequiredStreamsBoxed(ordinal);
    }

    public boolean getHasRequiredLanguage() {
        return delegate().getHasRequiredLanguage(ordinal);
    }

    public Boolean getHasRequiredLanguageBoxed() {
        return delegate().getHasRequiredLanguageBoxed(ordinal);
    }

    public boolean getHasLocalText() {
        return delegate().getHasLocalText(ordinal);
    }

    public Boolean getHasLocalTextBoxed() {
        return delegate().getHasLocalTextBoxed(ordinal);
    }

    public boolean getHasLocalAudio() {
        return delegate().getHasLocalAudio(ordinal);
    }

    public Boolean getHasLocalAudioBoxed() {
        return delegate().getHasLocalAudioBoxed(ordinal);
    }

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public RightsContractPackageTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected RightsContractPackageDelegate delegate() {
        return (RightsContractPackageDelegate)delegate;
    }

}