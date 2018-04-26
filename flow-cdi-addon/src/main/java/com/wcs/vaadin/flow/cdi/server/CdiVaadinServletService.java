package com.wcs.vaadin.flow.cdi.server;

import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.wcs.vaadin.flow.cdi.VaadinServiceEnabled;
import com.wcs.vaadin.flow.cdi.internal.BeanLookup;
import com.wcs.vaadin.flow.cdi.internal.VaadinSessionScopedContext;
import org.apache.deltaspike.core.util.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Optional;

import static com.wcs.vaadin.flow.cdi.internal.BeanLookup.SERVICE;

/**
 * Servlet service implementation for Vaadin CDI.
 * <p>
 * This class creates and initializes a @{@link VaadinServiceEnabled}
 * {@link Instantiator}, and
 * {@link SystemMessagesProvider} contextual instance.
 *
 * @see CdiVaadinServlet
 */
public class CdiVaadinServletService extends VaadinServletService {

    private final BeanManager beanManager;

    public CdiVaadinServletService(CdiVaadinServlet servlet,
                                   DeploymentConfiguration configuration,
                                   BeanManager beanManager) {
        super(servlet, configuration);
        this.beanManager = beanManager;
    }

    @Override
    public void init() throws ServiceException {
        lookupCdiService(SystemMessagesProvider.class)
                .ifPresent(this::setSystemMessagesProvider);
        addSessionInitListener(this::sessionInit);
        addSessionDestroyListener(this::sessionDestroy);
        super.init();
    }

    private void sessionInit(SessionInitEvent sessionInitEvent) {
        VaadinSession session = sessionInitEvent.getSession();
        getInstance(ErrorHandler.class).ifPresent(session::setErrorHandler);
        beanManager.fireEvent(sessionInitEvent);
    }

    @Override
    public CdiVaadinServlet getServlet() {
        return (CdiVaadinServlet) super.getServlet();
    }

    @Override
    protected Optional<Instantiator> loadInstantiators()
            throws ServiceException {
        Optional<Instantiator> instantiatorOptional =
                lookupCdiService(Instantiator.class);
        if (instantiatorOptional.isPresent()) {
            Instantiator instantiator = instantiatorOptional.get();
            if (!instantiator.init(this)) {
                Class unproxiedClass =
                        ProxyUtils.getUnproxiedClass(instantiator.getClass());
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
        return instantiatorOptional;
    }

    protected <T> Optional<T> lookupCdiService(Class<T> type) throws ServiceException {
        try {
            return getInstance(type);
        } catch (AmbiguousResolutionException e) {
            throw new ServiceException(
                    "Cannot init VaadinService because there are multiple "
                            + "eligible CDI " + type.getSimpleName()
                            + " beans.", e);
        }
    }

    private <T> Optional<T> getInstance(Class<T> type) {
        T instance = new BeanLookup<>(beanManager, type, SERVICE).get();
        return Optional.ofNullable(instance);
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CdiVaadinServletService.class
                .getCanonicalName());
    }

    private void sessionDestroy(SessionDestroyEvent sessionDestroyEvent) {
        beanManager.fireEvent(sessionDestroyEvent);
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
        VaadinSessionScopedContext.destroy(sessionDestroyEvent.getSession());
    }

}
