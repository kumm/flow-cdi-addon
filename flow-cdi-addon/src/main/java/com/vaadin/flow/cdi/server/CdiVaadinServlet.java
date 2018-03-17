package com.vaadin.flow.cdi.server;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

public class CdiVaadinServlet extends VaadinServlet {
    @Inject
    private BeanManager beanManager;

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration configuration) throws ServiceException {
        final CdiVaadinServletService service =
                new CdiVaadinServletService(this, configuration, beanManager);
        service.init();
        return service;
    }
}
