package com.netflix.hollow.api.producer.listener;

public interface DeltaChainForkListener {

    void onVersionValidationCheck(long restoredVersion);
}
