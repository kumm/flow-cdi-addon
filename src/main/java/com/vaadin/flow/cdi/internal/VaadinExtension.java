package com.vaadin.flow.cdi.internal;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import java.util.logging.Logger;

public class VaadinExtension implements Extension {

    public void initializeContexts(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        getLogger().info("Vaadin extension...");
    }

    private static Logger getLogger() {
        return Logger.getLogger(VaadinExtension.class.getCanonicalName());
    }
}
