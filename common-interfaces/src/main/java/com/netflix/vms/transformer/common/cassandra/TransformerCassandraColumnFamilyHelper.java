package com.netflix.vms.transformer.common.cassandra;

import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import java.util.Map;

public interface TransformerCassandraColumnFamilyHelper {
    void addVipKeyValuePair(String vip, String key, String value) throws ConnectionException;

    void addKeyValuePair(String key, String value) throws ConnectionException;

    void addKeyColumnValue(String key, String columnName, String value) throws ConnectionException;

    void deleteVipKeyValuePair(String vip, String key) throws ConnectionException;

    void deleteKeyValuePair(String key) throws ConnectionException;

    void deleteKeyColumnValue(String key, String columnName) throws ConnectionException;
    
    String getVipKeyValuePair(String vip, String key) throws ConnectionException;

    String getKeyValuePair(String key) throws ConnectionException;

    String getKeyColumnValue(String key, String columnName) throws ConnectionException;

    Map<String, String> getColumns(String key) throws ConnectionException;

    MutationBatch createMutationBatch();

    ColumnFamilyQuery<String, String> createQuery();

    ColumnFamily<String, String> getColumnFamily();

    String vipSpecificKey(String vip, String key);
}
