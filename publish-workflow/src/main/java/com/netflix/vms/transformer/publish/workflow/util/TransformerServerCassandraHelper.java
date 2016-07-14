package com.netflix.vms.transformer.publish.workflow.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.cassandra.NFAstyanaxManager;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TransformerServerCassandraHelper implements TransformerCassandraHelper {
    
    private static final String CLUSTER_NAME = "cass_dpt";
    
    private final NFAstyanaxManager astyanax;
    
    private final ConcurrentHashMap<TransformerColumnFamily, TransformerCassandraColumnFamilyHelper> map;
    
    @Inject
    public TransformerServerCassandraHelper(NFAstyanaxManager astyanax) {
        this.astyanax = astyanax;
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public TransformerCassandraColumnFamilyHelper getColumnFamilyHelper(TransformerColumnFamily cf) {
        TransformerCassandraColumnFamilyHelper helper = map.get(cf);
        if(helper == null) {
            helper = new TransformerServerCassandraColumnFamilyHelper(astyanax, CLUSTER_NAME, cf.getKeyspace(), cf.getColumnFamily());
            TransformerCassandraColumnFamilyHelper existingHelper = map.putIfAbsent(cf, helper);
            if(existingHelper != null)
                helper = existingHelper;
        }
        
        return helper;
    }
    
}
