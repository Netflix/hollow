package com.netflix.vms.transformer.publish;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.config.FastProperty;
import com.netflix.vms.transformer.common.TransformerContext;

public class CassandraBasedPoisonedStateMarker implements PoisonedStateMarker {

    private static final FastProperty.BooleanProperty CHECK_FOR_POISON_STATES = new FastProperty.BooleanProperty("com.netflix.videometadata.checkforpoisonstates", true);

    /* dependencies */
    private final TransformerContext ctx;

    /* fields */
    private final String vip;

    public CassandraBasedPoisonedStateMarker(TransformerContext ctx, String vip) {
        this.ctx = ctx;
        this.vip = vip;
    }

    public String getVip() {
        return vip;
    }

    @Override
    public void markStatePoisoned(long version, boolean isPoisoned) throws ConnectionException {
        ctx.getPoisonStatesHelper().addVipKeyValuePair(vip, String.valueOf(version), String.valueOf(isPoisoned));
    }

    @Override
    public boolean isStatePoisoned(long version) {
        if(!CHECK_FOR_POISON_STATES.get())
            return false;

        try {
            String poisonStatus = ctx.getPoisonStatesHelper().getVipKeyValuePair(vip, String.valueOf(version));
            if("true".equals(poisonStatus)) {
                // FIXME: what's the proper type to log?
                ctx.getLogger().info("poison_state_discovered", "VMS Poisoned state discovered -- " + version);
                return true;
            }
        } catch(NotFoundException nfe) {
            return false;
        } catch(Throwable th) {
            // FIXME: what type and message should we pass? Alternatively, why don't we let this bubble up to be logged
            // in a central exception handler?
            ctx.getLogger().error("", "", th);
        }

        return true;
    }
}
