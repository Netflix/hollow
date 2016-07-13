package com.netflix.vms.transformer.publish.status;

public interface CycleStatusFuture {

    public boolean awaitStatus();
    
    public static CycleStatusFuture UNCHECKED_STATUS = new CycleStatusFuture() {
        @Override
        public boolean awaitStatus() {
            return true;
        }
    };
    
}
