package com.netflix.hollow.api.common;

import com.netflix.hollow.api.producer.listener.VetoableListener;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public abstract class Listeners {

    private static final Logger LOG = Logger.getLogger(Listeners.class.getName());

    protected final EventListener[] listeners;

    protected Listeners(EventListener[] listeners) {
        this.listeners = listeners;
    }

    public <T extends EventListener> Stream<T> getListeners(Class<T> c) {
        return Arrays.stream(listeners).filter(c::isInstance).map(c::cast);
    }

    protected <T extends EventListener> void fire(
            Class<T> c, Consumer<? super T> r) {
        fireStream(getListeners(c), r);
    }

    protected <T extends EventListener> void fireStream(
            Stream<T> s, Consumer<? super T> r) {
        s.forEach(l -> {
            try {
                r.accept(l);
            } catch (VetoableListener.ListenerVetoException e) {
                throw e;
            } catch (RuntimeException e) {
                if (l instanceof VetoableListener) {
                    throw e;
                }
                LOG.log(Level.WARNING, "Error executing listener", e);
            }
        });
    }
}
