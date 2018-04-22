package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.wcs.vaadin.flow.cdi.internal.CdiUI;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Servlet to create CdiVaadinServletService.
 * <p>
 * An instance of this servlet is automatically deployed by
 * {@link CdiServletDeployer} if no VaadinServlet is deployed based on web.xml or
 * Servlet 3.0 annotations. A subclass of this servlet and of
 * {@link CdiVaadinServletService} can be used and explicitly deployed
 * to customize it, in which case
 * {@link #createServletService(DeploymentConfiguration)} must call
 * service.init() .
 */

public class CdiVaadinServlet extends VaadinServlet {
    @Inject
    private BeanManager beanManager;

    private static final ThreadLocal<ContextualStorage> currentContextualStorage =
            new ThreadLocal<>();

    private ContextualStorage contextualStorage;

    /**
     * Until VaadinService appears in CurrentInstance,
     * it have to be used instead of the non-static getter.
     * <p>
     * This method is meant for internal use only.
     *
     * @see VaadinServlet#getCurrent()
     * @return contextual storage for
     * @{@link com.wcs.vaadin.flow.cdi.VaadinServiceScoped} context.
     */
    public static ContextualStorage getCurrentContextualStorage() {
        return currentContextualStorage.get();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        initContextualStorage();
        try {
            currentContextualStorage.set(contextualStorage);
            super.init(servletConfig);
        } finally {
            currentContextualStorage.set(null);
        }
    }

    protected void initContextualStorage() {
        contextualStorage = new ContextualStorage(beanManager, true, true);
    }

    /**
     * Contextual storage for
     * @{@link com.wcs.vaadin.flow.cdi.VaadinServiceScoped} context.
     * <p>
     * This method is meant for internal use only.
     *
     * @return contextual storage
     */
    public ContextualStorage getContextualStorage() {
        return contextualStorage;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            currentContextualStorage.set(contextualStorage);
            super.service(request, response);
        } finally {
            currentContextualStorage.set(null);
        }
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration configuration) throws ServiceException {
        final CdiVaadinServletService service =
                new CdiVaadinServletService(this, configuration, beanManager);
        service.init();
        return service;
    }

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration(
            Properties initParameters) {
        return new CdiDeploymentConfiguration(getClass(), initParameters,
                this::scanForResources);
    }

    private static class CdiDeploymentConfiguration extends DefaultDeploymentConfiguration {
        public CdiDeploymentConfiguration(Class<?> systemPropertyBaseClass, Properties initParameters, BiConsumer<String, Predicate<String>> resourceScanner) {
            super(systemPropertyBaseClass, initParameters, resourceScanner);
        }

        @Override
        public String getUIClassName() {
            return getStringProperty(VaadinSession.UI_PARAMETER,
                    CdiUI.class.getName());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        AbstractContext.destroyAllActive(contextualStorage);
    }
}
