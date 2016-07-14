package com.netflix.vms.transformer.common.cassandra;

public interface TransformerCassandraHelper {
    
    public TransformerCassandraColumnFamilyHelper getColumnFamilyHelper(String keyspace, String columnFamily);
    
}
