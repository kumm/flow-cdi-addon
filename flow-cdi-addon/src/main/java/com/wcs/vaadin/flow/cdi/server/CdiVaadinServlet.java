package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
/**
 * Servlet to create CdiVaadinServletService.
 * <p>
 * An instance of this servlet is automatically deployed by
 * {@link CdiServletDeployer} if no VaadinServlet is deployed based on web.xml or
 * Servlet 3.0 annotations. A subclass of this servlet and of
 * {@link CdiVaadinServletService} can be used and explicitly deployed to customize e.g.
 * system messages, in which case
 * {@link #createServletService(DeploymentConfiguration)} must call
 * service.init() .
 */

public class CdiVaadinServlet extends VaadinServlet {
    @Inject
    private BeanManager beanManager;

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration configuration) throws ServiceException {
        final CdiVaadinServletService service =
                new CdiVaadinServletService(this, configuration, beanManager);

        // Need an active service context during init.
        VaadinService.setCurrent(service);
        service.init();
        VaadinService.setCurrent(null);
        return service;
    }
}
