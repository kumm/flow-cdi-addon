package com.wcs.vaadin.flow.cdi.contexts;

import com.vaadin.flow.server.ServiceDestroyEvent;
import com.vaadin.flow.server.VaadinService;
import com.wcs.vaadin.flow.cdi.internal.VaadinServiceScopedContext.ContextualStorageManager;
import com.wcs.vaadin.flow.cdi.server.CdiVaadinServletService;
import com.wcs.vaadin.flow.cdi.server.TestCdiVaadinServletService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

public class ServiceUnderTestContext implements UnderTestContext {
    private CdiVaadinServletService service;
    private static int NDX;
    private final BeanManager beanManager;

    public ServiceUnderTestContext(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public void activate() {
        service = new TestCdiVaadinServletService(beanManager, ++NDX + "");
        VaadinService.setCurrent(service);
    }

    @Override
    public void tearDownAll() {
        VaadinService.setCurrent(null);
        Context appContext = beanManager.getContext(ApplicationScoped.class);
        Set<Bean<?>> beans = beanManager.getBeans(ContextualStorageManager.class);
        ((AlterableContext) appContext).destroy(beanManager.resolve(beans));
    }

    @Override
    public void destroy() {
        if (service != null) {
            beanManager.fireEvent(new ServiceDestroyEvent(service));
        }
    }

    public CdiVaadinServletService getService() {
        return service;
    }
}
