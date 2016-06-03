package com.netflix.vms.transformer.util;

import java.util.HashMap;
import java.util.Map;

import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import com.netflix.astyanax.retry.RetryNTimes;
import com.netflix.astyanax.retry.RetryPolicy;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.cassandra.NFAstyanaxManager;
import com.netflix.vms.transformer.common.publish.workflow.TransformerCassandraHelper;

public class TransformerServerCassandraHelper implements TransformerCassandraHelper {

    private final Keyspace keyspace;
    private final ColumnFamily<String, String> columnFamily;

    public TransformerServerCassandraHelper(NFAstyanaxManager astyanaxManager, String clusterName, String keyspaceName, String columnFamilyName) {
        this.keyspace = getKeyspace(astyanaxManager, clusterName, keyspaceName);
        this.columnFamily = new ColumnFamily<String, String>(columnFamilyName, StringSerializer.get(), StringSerializer.get());
    }


    @Override
    public void addVipKeyValuePair(String vip, String key, String value) throws ConnectionException {
        addKeyValuePair(vipSpecificKey(vip, key), value);
    }

    @Override
    public void addKeyValuePair(String key, String value) throws ConnectionException {
        addKeyColumnValue(key, "val", value);
    }

    @Override
    public void addKeyColumnValue(String key, String columnName, String value) throws ConnectionException {
        MutationBatch batch = createMutationBatch();
        batch.withRow(getColumnFamily(), key).putColumn(columnName, value);
        batch.execute();
    }

    @Override
    public void deleteVipKeyValuePair(String vip, String key) throws ConnectionException {
        deleteKeyValuePair(vipSpecificKey(vip, key));
    }

    @Override
    public void deleteKeyValuePair(String key) throws ConnectionException {
        deleteKeyColumnValue(key, "val");
    }

    @Override
    public void deleteKeyColumnValue(String key, String columnName) throws ConnectionException {
        MutationBatch batch = createMutationBatch();
        batch.withRow(getColumnFamily(), key).deleteColumn(columnName);
        batch.execute();
    }

    @Override
    public String getVipKeyValuePair(String vip, String key) throws ConnectionException {
        return getKeyValuePair(vipSpecificKey(vip, key));
    }

    @Override
    public String getKeyValuePair(String key) throws ConnectionException {
        return getKeyColumnValue(key, "val");
    }

    @Override
    public String getKeyColumnValue(String key, String columnName) throws ConnectionException {
        return createQuery().getKey(key).getColumn(columnName).execute().getResult().getStringValue();
    }

    @Override
    public Map<String, String> getColumns(String key) throws ConnectionException {
        OperationResult<ColumnList<String>> opResult = createQuery().getKey(key).execute();

        ColumnList<String> result = opResult.getResult();

        Map<String, String> resultMap = new HashMap<String, String>();

        for(int i=0;i<result.size();i++) {
            Column<String> column = result.getColumnByIndex(i);
            resultMap.put(column.getName(), column.getStringValue());
        }

        return resultMap;
    }

    @Override
    public MutationBatch createMutationBatch() {
        return this.keyspace.prepareMutationBatch().withConsistencyLevel(ConsistencyLevel.CL_LOCAL_QUORUM)
                .withRetryPolicy(getRetryPolicy());
    }

    @Override
    public ColumnFamilyQuery<String, String> createQuery() {
        return this.keyspace.prepareQuery(columnFamily).setConsistencyLevel(ConsistencyLevel.CL_LOCAL_QUORUM)
                .withRetryPolicy(getRetryPolicy());
    }

    @Override
    public ColumnFamily<String, String> getColumnFamily() {
        return columnFamily;
    }

    private RetryPolicy getRetryPolicy() {
        return new RetryNTimes(3);
    }

    @Override
    public String vipSpecificKey(String vip, String key) {
        return key + "_" + vip;
    }

    private Keyspace getKeyspace(NFAstyanaxManager astyanaxManager, String clusterName, String keyspaceName) {
        Keyspace keyspace = astyanaxManager.getRegisteredKeyspace(clusterName, keyspaceName);
        if (keyspace == null) {
            try {
                keyspace = astyanaxManager.registerKeyspace(clusterName, keyspaceName);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return keyspace;
    }

}
