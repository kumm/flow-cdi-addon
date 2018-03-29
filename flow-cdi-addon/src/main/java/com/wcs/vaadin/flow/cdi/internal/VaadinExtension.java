package com.wcs.vaadin.flow.cdi.internal;

import com.wcs.vaadin.flow.cdi.NormalUIScoped;
import com.wcs.vaadin.flow.cdi.UIScoped;
import com.wcs.vaadin.flow.cdi.VaadinSessionScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * CDI Extension needed to register Vaadin scopes to the runtime.
 */
public class VaadinExtension implements Extension {

    private UIScopedContext uiScopedContext;

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        uiScopedContext.init(beanManager);
    }

    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {
        uiScopedContext = new UIScopedContext(beanManager);
        afterBeanDiscovery.addContext(new ContextWrapper(uiScopedContext,
                UIScoped.class));
        afterBeanDiscovery.addContext(new ContextWrapper(uiScopedContext,
                NormalUIScoped.class));
        getLogger().info("UIScopedContext registered for Vaadin CDI");


        VaadinSessionScopedContext vaadinSessionScopedContext =
                new VaadinSessionScopedContext(beanManager);
        afterBeanDiscovery.addContext(
                new ContextWrapper(vaadinSessionScopedContext, VaadinSessionScoped.class));
        getLogger().info("VaadinSessionScopedContext registered for Vaadin CDI");
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(VaadinExtension.class.getCanonicalName());
    }
}
