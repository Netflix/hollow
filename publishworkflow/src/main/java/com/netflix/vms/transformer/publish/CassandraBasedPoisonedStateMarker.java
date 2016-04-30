package com.netflix.vms.transformer.publish;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.config.FastProperty;
import com.netflix.logging.ILog;
import com.netflix.logging.LogManager;

public class CassandraBasedPoisonedStateMarker implements PoisonedStateMarker {

    private static final ILog LOGGER = LogManager.getLogger(CassandraBasedPoisonedStateMarker.class);

    private static final FastProperty.BooleanProperty CHECK_FOR_POISON_STATES = new FastProperty.BooleanProperty("com.netflix.videometadata.checkforpoisonstates", true);

    private static final String CASSANDRA_CLUSTER_NAME = "cass_dpt";
    private static final String CASSANDRA_KEYSPACE_NAME = "vms_poison_states";
    private static final String CASSANDRA_COLUMN_FAMILY_NAME = "poison_states";

    private final String vip;
    private final VMSCassandraHelper cassandraHelper;

    public CassandraBasedPoisonedStateMarker(String vip) {
        this.cassandraHelper = new VMSCassandraHelper(CASSANDRA_CLUSTER_NAME, CASSANDRA_KEYSPACE_NAME, CASSANDRA_COLUMN_FAMILY_NAME);
        this.vip = vip;
    }

    public String getVip() {
        return vip;
    }

    public void markStatePoisoned(long version, boolean isPoisoned) throws ConnectionException {
        cassandraHelper.addVipKeyValuePair(vip, String.valueOf(version), String.valueOf(isPoisoned));
    }

    @Override
    public boolean isStatePoisoned(long version) {
        if(!CHECK_FOR_POISON_STATES.get())
            return false;

        try {
            String poisonStatus = cassandraHelper.getVipKeyValuePair(vip, String.valueOf(version));
            if("true".equals(poisonStatus)) {
                LOGGER.info("VMS Poisoned state discovered -- " + version);
                return true;
            }
        } catch(NotFoundException nfe) {
            return false;
        } catch(Throwable th) {
            LOGGER.error(th);
        }

        return true;
    }

}
