package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.wcs.vaadin.flow.cdi.internal.BeanLookup;
import com.wcs.vaadin.flow.cdi.internal.VaadinServiceScopedContext;
import com.wcs.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.apache.deltaspike.core.util.context.ContextualStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Optional;

import static com.wcs.vaadin.flow.cdi.internal.BeanLookup.SERVICE;

/**
 * Servlet service implementation for Vaadin CDI.
 * <p>
 * This class creates and initializes a
 * @{@link VaadinServiceEnabled} CDI {@link Instantiator} contextual instance.
 *
 * @see CdiVaadinServlet
 */
public class CdiVaadinServletService extends VaadinServletService {

    private final BeanManager beanManager;
    private final ContextualStorage contextualStorage;

    public CdiVaadinServletService(VaadinServlet servlet,
                                   DeploymentConfiguration configuration,
                                   BeanManager beanManager) {
        super(servlet, configuration);
        this.beanManager = beanManager;
        contextualStorage = new ContextualStorage(beanManager, true, true);
        addSessionDestroyListener(this::sessionDestroy);
    }

    public ContextualStorage getContextualStorage() {
        return contextualStorage;
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Instantiator cdiInstantiator;
        try {
            cdiInstantiator = new BeanLookup<>(beanManager, Instantiator.class, SERVICE)
                    .ifAmbiguous(e -> {
                        throw e;
                    })
                    .get();
        } catch (AmbiguousResolutionException e) {
            throw new ServiceException(
                    "Cannot init VaadinService because there are multiple "
                            + "eligible CDI instantiator beans.", e);
        }
        if (cdiInstantiator != null) {
            if (!cdiInstantiator.init(this)) {
                Class unproxiedClass =
                        ProxyUtils.getUnproxiedClass(cdiInstantiator.getClass());
                throw new ServiceException(
                        "Cannot init VaadinService because "
                                + unproxiedClass.getName() + " CDI bean init()"
                                + " returned false.");
            }
        } else {
            throw new ServiceException(
                    "Cannot init VaadinService "
                            + "because no CDI instantiator bean found."
            );
        }
        return Optional.of(cdiInstantiator);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CdiVaadinServletService.class
                .getCanonicalName());
    }

    private void sessionDestroy(SessionDestroyEvent event) {
        if (VaadinSessionScopedContext.guessContextIsUndeployed()) {
            // Happens on tomcat when it expires sessions upon undeploy.
            // beanManager.getPassivationCapableBean returns null for passivation id,
            // so we would get an NPE from AbstractContext.destroyAllActive
            getLogger().warn("VaadinSessionScoped context does not exist. " +
                    "Maybe application is undeployed." +
                    " Can't destroy VaadinSessionScopedContext.");
            return;
        }
        getLogger().debug("VaadinSessionScopedContext destroy");
        VaadinSessionScopedContext.destroy(event.getSession());
    }

    @Override
    public void destroy() {
        super.destroy();
        VaadinServiceScopedContext.destroy(this);
    }
}
