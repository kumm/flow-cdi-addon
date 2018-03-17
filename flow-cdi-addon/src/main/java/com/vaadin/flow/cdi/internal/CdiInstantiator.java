package com.vaadin.flow.cdi.internal;

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.server.VaadinService;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.inject.spi.BeanManager;

public class CdiInstantiator extends DefaultInstantiator {

    private final BeanManager beanManager;

    public CdiInstantiator(VaadinService service, BeanManager beanManager) {
        super(service);
        this.beanManager = beanManager;
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        final T reference = BeanProvider
                .getContextualReference(beanManager, type, true);
        if (reference != null) {
            return reference;
        } else {
            return super.getOrCreate(type);
        }
    }
}
