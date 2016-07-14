package com.netflix.vms.transformer.publish.workflow.util;

import com.netflix.cassandra.NFAstyanaxManager;

import com.google.inject.Inject;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import java.util.concurrent.ConcurrentHashMap;
import com.google.inject.Singleton;

@Singleton
public class TransformerServerCassandraHelper implements TransformerCassandraHelper {
    
    private static final String CLUSTER_NAME = "cass_dpt";
    
    private final NFAstyanaxManager astyanax;
    
    private final ConcurrentHashMap<String, TransformerCassandraColumnFamilyHelper> map;
    
    @Inject
    public TransformerServerCassandraHelper(NFAstyanaxManager astyanax) {
        this.astyanax = astyanax;
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public TransformerCassandraColumnFamilyHelper getColumnFamilyHelper(String keyspace, String columnFamily) {
        String cfKey = keyspace + "_" + columnFamily;
        TransformerCassandraColumnFamilyHelper helper = map.get(cfKey);
        if(helper == null) {
            helper = new TransformerServerCassandraColumnFamilyHelper(astyanax, CLUSTER_NAME, keyspace, columnFamily);
            TransformerCassandraColumnFamilyHelper existingHelper = map.putIfAbsent(cfKey, helper);
            if(existingHelper != null)
                helper = existingHelper;
        }
        
        return helper;
    }
    
}
