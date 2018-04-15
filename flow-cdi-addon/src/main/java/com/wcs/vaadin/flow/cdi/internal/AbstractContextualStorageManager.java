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

/**
 * Base class for manage and store ContextualStorages.
 *
 * This class is responsible for
 * - creating, and providing the ContextualStorage for a context key
 * - destroying ContextualStorages
 *
 * Concurrency handling ignored intentionally.
 * Locking of VaadinSession is the responsibility of Vaadin Framework.
 */
abstract class AbstractContextualStorageManager<K> implements Serializable  {
    @Inject
    private BeanManager beanManager;
    private final Map<K, ContextualStorage> storageMap = new HashMap<>();

    protected ContextualStorage getContextualStorage(K key, boolean createIfNotExist) {
        ContextualStorage storage = storageMap.get(key);
        if (storage == null && createIfNotExist) {
            storage = new VaadinContextualStorage(beanManager);
            storageMap.put(key, storage);
        }
        return storage;
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
