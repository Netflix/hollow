package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsContractPackageDelegate extends HollowObjectDelegate {

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public boolean getPrimary(int ordinal);

    public Boolean getPrimaryBoxed(int ordinal);

    public boolean getHasRequiredStreams(int ordinal);

    public Boolean getHasRequiredStreamsBoxed(int ordinal);

    public boolean getHasRequiredLanguage(int ordinal);

    public Boolean getHasRequiredLanguageBoxed(int ordinal);

    public boolean getHasLocalText(int ordinal);

    public Boolean getHasLocalTextBoxed(int ordinal);

    public boolean getHasLocalAudio(int ordinal);

    public Boolean getHasLocalAudioBoxed(int ordinal);

    public RightsContractPackageTypeAPI getTypeAPI();

}