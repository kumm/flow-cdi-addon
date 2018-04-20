package com.wcs.vaadin.flow.cdi.itest.service;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.VaadinServiceScoped;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;
import java.util.stream.Stream;

@Priority(Interceptor.Priority.APPLICATION)
@Alternative
@VaadinServiceEnabled
@VaadinServiceScoped
public class InstantiatorAlternative implements Instantiator {

    @Override
    public boolean init(VaadinService service) {
        return true;
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        return Stream.of();
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        T instance = BeanProvider.getContextualReference(type, true);
        if (InstantiatorCustomizedView.class.equals(type)) {
            ((InstantiatorCustomizedView) instance).customize();
        }
        return instance;
    }
}
