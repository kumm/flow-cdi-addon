package com.wcs.vaadin.flow.cdi.internal;

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for manage and store ContextualStorages.
 *
 * This class is responsible for
 * - creating, and providing the ContextualStorage for a context key
 * - destroying ContextualStorages
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
abstract class AbstractContextualStorageManager<K> implements Serializable  {
    @Inject
    private BeanManager beanManager;
    private final boolean concurrent;
    private final Map<K, ContextualStorage> storageMap;

    protected AbstractContextualStorageManager(boolean concurrent) {
        if (concurrent) {
            this.storageMap = new ConcurrentHashMap<>();
        } else {
            this.storageMap = new HashMap<>();
        }
        this.concurrent = concurrent;
    }

    protected ContextualStorage getContextualStorage(K key, boolean createIfNotExist) {
        if (createIfNotExist) {
            return storageMap.computeIfAbsent(key, this::newContextualStorage);
        } else {
            return storageMap.get(key);
        }
    }

    private ContextualStorage newContextualStorage(K key) {
        // Not required by the spec, but in reality beans are PassivationCapable.
        // Even for non serializable bean classes.
        // CDI implementations use PassivationCapable beans,
        // because injecting non serializable proxies might block serialization of
        // bean instances in a passivation capable context.
        return new ContextualStorage(beanManager, concurrent, true);
    }

    protected boolean isExist(K key) {
        return storageMap.containsKey(key);
    }

    @PreDestroy
    protected void destroyAll() {
        Collection<ContextualStorage> storages = storageMap.values();
        for (ContextualStorage storage : storages) {
            AbstractContext.destroyAllActive(storage);
        }
        storageMap.clear();
    }

    protected void destroy(K key) {
        ContextualStorage storage = storageMap.remove(key);
        if (storage != null) {
            AbstractContext.destroyAllActive(storage);
        }
    }

    protected Map<K, ContextualStorage> getAll() {
        return storageMap;
    }
}
