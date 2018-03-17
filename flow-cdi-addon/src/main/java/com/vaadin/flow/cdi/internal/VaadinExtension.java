package com.vaadin.flow.cdi.internal;

import com.vaadin.flow.cdi.VaadinSessionScoped;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import java.util.logging.Logger;

public class VaadinExtension implements Extension {

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        getLogger().info("Vaadin extension...");
    }

    void afterBeanDiscovery(
            @Observes final AfterBeanDiscovery afterBeanDiscovery,
            final BeanManager beanManager) {
        VaadinSessionScopedContext vaadinSessionScopedContext =
                new VaadinSessionScopedContext(beanManager);
        afterBeanDiscovery.addContext(
                new ContextWrapper(vaadinSessionScopedContext, VaadinSessionScoped.class));
        getLogger().info("VaadinSessionScopedContext registered for Vaadin CDI");
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinExtension.class.getCanonicalName());
    }
}
