package com.netflix.vms.transformer.publish.poison;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.ObservedPoisonState;

import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.config.FastProperty;
import com.netflix.vms.transformer.common.TransformerContext;

public class CassandraBasedPoisonedStateMarker implements PoisonedStateMarker {

    private static final FastProperty.BooleanProperty CHECK_FOR_POISON_STATES = new FastProperty.BooleanProperty("com.netflix.videometadata.checkforpoisonstates", true);

    /* dependencies */
    private final TransformerContext ctx;
    private final TransformerCassandraColumnFamilyHelper cassandraHelper;

    /* fields */
    private final String vip;

    public CassandraBasedPoisonedStateMarker(TransformerContext ctx, String vip) {
        this.ctx = ctx;
        this.vip = vip;
        this.cassandraHelper = ctx.getCassandraHelper().getColumnFamilyHelper("vms_poison_states", "poison_states");
    }

    public String getVip() {
        return vip;
    }

    @Override
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
                ctx.getLogger().info(ObservedPoisonState, "VMS Poisoned state discovered -- {}", version);
                return true;
            }
        } catch(NotFoundException nfe) {
            return false;
        } catch(Throwable th) {
            ctx.getLogger().error(ObservedPoisonState, "VMS Poisoned state discovery failed -- assuming poisoned -- {}", version, th);
        }

        return true;
    }
}
