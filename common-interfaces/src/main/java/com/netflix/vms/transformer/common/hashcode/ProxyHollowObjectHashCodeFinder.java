package com.netflix.vms.transformer.common.hashcode;

import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.Set;

public class ProxyHollowObjectHashCodeFinder implements HollowObjectHashCodeFinder {

    private HollowObjectHashCodeFinder delegateTo;

    public ProxyHollowObjectHashCodeFinder(HollowObjectHashCodeFinder delegateTo) {
        this.delegateTo = delegateTo;
    }

    public HollowObjectHashCodeFinder getDelegateTo() {
        return delegateTo;
    }

    public void swap(HollowObjectHashCodeFinder delegateTo) {
        this.delegateTo = delegateTo;
    }

    @Override
    public int hashCode(Object objectToHash) {
        return delegateTo.hashCode(objectToHash);
    }

    @Override
    public int hashCode(String typeName, int ordinal, Object objectToHash) {
        return delegateTo.hashCode(typeName, ordinal, objectToHash);
    }

    @Override
    public Set<String> getTypesWithDefinedHashCodes() {
        return delegateTo.getTypesWithDefinedHashCodes();
    }

    @Override
    public int hashCode(int ordinal, Object objectToHash) {
        return delegateTo.hashCode(ordinal, objectToHash);
    }

}