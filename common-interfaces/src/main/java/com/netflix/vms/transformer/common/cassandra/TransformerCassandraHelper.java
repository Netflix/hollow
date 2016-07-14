package com.netflix.vms.transformer.common.cassandra;

public interface TransformerCassandraHelper {
    
    public TransformerCassandraColumnFamilyHelper getColumnFamilyHelper(TransformerColumnFamily cf);
    
    public static enum TransformerColumnFamily {
        CANARY_VALIDATION("canary_validation", "canary_results"),
        CIRCUITBREAKER_STATS("hollow_publish_workflow", "hollow_validation_stats"),
        POISON_STATES("vms_poison_states", "poison_states"),
        ANNOUNCED_VERSIONS("vms_announced_versions", "vms_announced_versions"),
        DEV_SLICED_BLOB_IDS("vms_devslice_ids", "vms_devslice_ids");
        
        private final String keyspace;
        private final String columnFamily;
        
        private TransformerColumnFamily(String keyspace, String columnFamily) {
            this.keyspace = keyspace;
            this.columnFamily = columnFamily;
        }

        public String getKeyspace() {
            return keyspace;
        }

        public String getColumnFamily() {
            return columnFamily;
        }
    }
    
}
